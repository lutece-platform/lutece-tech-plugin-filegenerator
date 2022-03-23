/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.filegenerator.service;

import fr.paris.lutece.plugins.filegenerator.business.TemporaryFile;
import fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class TemporaryFileService
{
    private static final TemporaryFileService INSTANCE = new TemporaryFileService( );

    private IFileStoreServiceProvider _fileStoreServiceProvider;

    private TemporaryFileService( )
    {
        _fileStoreServiceProvider = FileService.getInstance( )
                .getFileStoreServiceProvider( AppPropertiesService.getProperty( "temporaryfiles.file.provider.service" ) );
    }

    public static final TemporaryFileService getInstance( )
    {
        return INSTANCE;
    }

    public int initTemporaryFile( AdminUser user, String description )
    {
        TemporaryFile file = new TemporaryFile( );
        file.setTitle( "temp" );
        file.setUser( user );
        file.setDescription( description );
        TemporaryFileHome.create( file );
        return file.getIdFile( );
    }

    public String savePhysicalFile( TemporaryFile tempFile, PhysicalFile physicalFile )
    {
        File file = new File( );
        file.setTitle( tempFile.getTitle( ) );
        file.setMimeType( tempFile.getMimeType( ) );
        file.setDateCreation( tempFile.getDateCreation( ) );
        file.setSize( tempFile.getSize( ) );
        file.setPhysicalFile( physicalFile );
        return _fileStoreServiceProvider.storeFile( file );
    }

    public PhysicalFile loadPhysicalFile( String idFile )
    {
        File file = _fileStoreServiceProvider.getFile( idFile );
        if ( file != null )
        {
            return file.getPhysicalFile( );
        }
        return null;
    }

    public void removeTemporaryFile( TemporaryFile temporaryFile )
    {
        if ( temporaryFile.getIdPhysicalFile( ) != null )
        {
            _fileStoreServiceProvider.delete( temporaryFile.getIdPhysicalFile( ) );
        }
        TemporaryFileHome.remove( temporaryFile.getIdFile( ) );
    }
}
