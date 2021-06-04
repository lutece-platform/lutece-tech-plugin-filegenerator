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

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import fr.paris.lutece.plugins.filegenerator.business.TemporaryFile;
import fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.file.FileUtil;

/**
 * Service tha generates and saves Temporary Files.
 */
public class TemporaryFileGeneratorService
{
    private static final TemporaryFileGeneratorService INSTANCE = new TemporaryFileGeneratorService( );
    private static final Integer FILE_MAX_SIZE = Integer.parseInt( AppPropertiesService.getProperty( "temporaryfiles.max.size", "0" ) );
    private static final String KEY_FILE_TOO_BIG = "filegenerator.temporaryfile.file.too.big";
    private static final Object LOCK = new Object( );

    public static TemporaryFileGeneratorService getInstance( )
    {
        return INSTANCE;
    }

    public void generateFile( IFileGenerator generator, AdminUser user )
    {
        CompletableFuture.runAsync( new GenerateFileRunnable( generator, user ) );
    }

    private static final class GenerateFileRunnable implements Runnable
    {

        private IFileGenerator _generator;
        private AdminUser _user;

        /**
         * Constructor.
         * 
         * @param _generator
         * @param _user
         */
        public GenerateFileRunnable( IFileGenerator generator, AdminUser user )
        {
            _generator = generator;
            _user = user;
        }

        @Override
        public void run( )
        {
            int idFile = TemporaryFileService.getInstance( ).initTemporaryFile( _user, _generator.getDescription( ) );
            synchronized( LOCK )
            {
                Path generatedFile = null;
                try
                {
                    generatedFile = _generator.generateFile( );
                }
                catch( IOException e )
                {
                    AppLogService.error( "Error generating temporary file with id " + idFile, e );
                }
                updateTemporaryFile( generatedFile, idFile );
            }
        }

        private void updateTemporaryFile( Path generatedFile, int idFile )
        {
            TemporaryFile file = TemporaryFileHome.findByPrimaryKey( idFile );
            if ( generatedFile != null )
            {
                try
                {
                    PhysicalFile physicalFile = createPhysicalFile( generatedFile );
                    int size = physicalFile.getValue( ).length;
                    if ( FILE_MAX_SIZE > 0 && size > FILE_MAX_SIZE )
                    {
                        file.setTitle( I18nService.getLocalizedString( KEY_FILE_TOO_BIG, Locale.getDefault( ) ) );
                        AppLogService.error( "File too big ( " + size + ") : Max size is " + FILE_MAX_SIZE );
                    }
                    else
                    {
                        file.setTitle( _generator.getFileName( ) );
                        file.setMimeType( _generator.getMimeType( ) );
                        file.setDescription( _generator.getDescription( ) );
                        String physicaId = TemporaryFileService.getInstance( ).savePhysicalFile( file, physicalFile );
                        file.setIdPhysicalFile( physicaId );
                    }
                    file.setSize( size );
                }
                catch( IOException e )
                {
                    AppLogService.error( "Error storing temporary file with id " + idFile );
                    file.setTitle( "" );
                    file.setSize( -1 );
                }
                finally
                {
                    if ( generatedFile.toFile( ).isDirectory( ) )
                    {
                        for ( File fileToDelete : generatedFile.toFile( ).listFiles( ) )
                        {
                            FileUtil.deleteFile( fileToDelete );
                        }
                    }
                    FileUtil.deleteFile( generatedFile.toFile( ) );
                }
            }
            else
            {
                file.setSize( -1 );
            }

            TemporaryFileHome.update( file );
        }

        private PhysicalFile createPhysicalFile( Path generatedFile ) throws IOException
        {
            PhysicalFile physicalFile = new PhysicalFile( );
            if ( _generator.hasMultipleFiles( ) )
            {
                List<Path> files = new ArrayList<>( );
                try ( DirectoryStream<Path> stream = Files.newDirectoryStream( generatedFile ) )
                {
                    for ( Path path : stream )
                    {
                        files.add( path );
                    }
                }
                createZipPhysicalFile( generatedFile, physicalFile, files.toArray( new Path [ files.size( )] ) );
            }
            else
                if ( _generator.isZippable( ) )
                {
                    createZipPhysicalFile( generatedFile, physicalFile, generatedFile );
                }
                else
                {
                    physicalFile.setValue( Files.readAllBytes( generatedFile ) );
                }
            return physicalFile;
        }

        private void createZipPhysicalFile( Path generatedFile, PhysicalFile physicalFile, Path... filesToZip ) throws IOException
        {
            Path zipFile = Paths.get( generatedFile.getParent( ).toString( ), _generator.getFileName( ) );
            try
            {
                FileUtil.zipFiles( zipFile, filesToZip );
                physicalFile.setValue( Files.readAllBytes( zipFile ) );
            }
            finally
            {
                FileUtil.deleteFile( zipFile.toFile( ) );
            }
        }
    }

}
