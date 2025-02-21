/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.filegenerator.business;

import java.util.List;

import fr.paris.lutece.plugins.filegenerator.service.FileGeneratorPlugin;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import jakarta.enterprise.inject.spi.CDI;

/**
 * This class provides instances management methods (create, find, ...) for file objects
 */
public final class TemporaryFileHome
{
    // Static variable pointed at the DAO instance
    private static ITemporaryFileDAO _dao = CDI.current( ).select( ITemporaryFileDAO.class ).get( );
    private static Plugin _plugin = FileGeneratorPlugin.getPlugin( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TemporaryFileHome( )
    {
    }

    /**
     * Creation of an instance of record file
     *
     * @param file
     *            The instance of the file which contains the informations to store
     * @return the id of the file after creation
     *
     */
    public static int create( TemporaryFile file )
    {
        return _dao.insert( file, _plugin );
    }

    /**
     * Update of file which is specified in parameter
     *
     * @param file
     *            The instance of the record file which contains the informations to update
     */
    public static void update( TemporaryFile file )
    {
        _dao.store( file, _plugin );
    }

    /**
     * Delete the file whose identifier is specified in parameter
     *
     * @param nIdFile
     *            The identifier of the record file
     */
    public static void remove( int nIdFile )
    {
        _dao.delete( nIdFile, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a file whose identifier is specified in parameter
     *
     * @param nKey
     *            The file primary key
     * @return an instance of file
     */
    public static TemporaryFile findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns a list of files belonging to an user
     *
     * @param user
     *            The user
     * @return a list of files
     */
    public static List<TemporaryFile> findByUser( AdminUser user )
    {
        return _dao.findByUser( user, _plugin );
    }

    /**
     * Select the temporary files older than the given number of days
     * 
     * @param days
     */
    public static List<TemporaryFile> selectFilesOlderThan( int days )
    {
        return _dao.selectFilesOlderThan( days, _plugin );
    }
}
