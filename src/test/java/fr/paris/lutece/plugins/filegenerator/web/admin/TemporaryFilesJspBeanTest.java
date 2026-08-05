/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.filegenerator.web.admin;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.filegenerator.business.TemporaryFile;
import fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.Utils;


public class TemporaryFilesJspBeanTest extends LuteceTestCase
{

    private static final String KEY_ONSTARTUP_DS = "core.daemon.temporaryfilesDaemon.onStartUp";
    private static final String KEY_DAYS_BEFORE_DELETE = "daemon.temporaryfilesDaemon.days.defore.delete";

    private static final String EXPECTED_MESSAGE_FRAGMENT = "avant suppression";

    private MockHttpServletRequest _request;
    private AdminUser _user;
    private String _savedDaysBeforeDelete;

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );


        DatastoreService.removeInstanceData( KEY_ONSTARTUP_DS );

        _savedDaysBeforeDelete = System.getProperty( KEY_DAYS_BEFORE_DELETE );

        _user = new AdminUser( );
        _user.setLocale( Locale.FRENCH );

        _request = new MockHttpServletRequest( );
        Utils.registerAdminUserWithRigth( _request, _user, TemporaryFilesJspBean.VIEW_TEMP_FILES );
    }

    @Override
    protected void tearDown( ) throws Exception
    {
        DatastoreService.removeInstanceData( KEY_ONSTARTUP_DS );
        restoreSystemProperty( KEY_DAYS_BEFORE_DELETE, _savedDaysBeforeDelete );
        super.tearDown( );
    }

    private static void restoreSystemProperty( String strKey, String strSavedValue )
    {
        if ( strSavedValue != null )
        {
            System.setProperty( strKey, strSavedValue );
        }
        else
        {
            System.clearProperty( strKey );
        }
    }

    public void testGetTemporaryFiles_daemonActivated_pageContainsDaysBeforeDeleteMessage( ) throws AccessDeniedException {
        DatastoreService.setInstanceDataValue( KEY_ONSTARTUP_DS, DatastoreService.VALUE_TRUE );
        System.setProperty( KEY_DAYS_BEFORE_DELETE, "42" );


        assertEquals( "The value written to the Datastore must be readable back immediately",
                DatastoreService.VALUE_TRUE, DatastoreService.getInstanceDataValue( KEY_ONSTARTUP_DS, DatastoreService.VALUE_FALSE ) );

        TemporaryFile file = new TemporaryFile( );
        file.setUser( _user );
        file.setDescription( "test-file" );
        TemporaryFileHome.create( file );

        try
        {
            TemporaryFilesJspBean bean = new TemporaryFilesJspBean( );
            bean.init( _request, TemporaryFilesJspBean.VIEW_TEMP_FILES );

            String html = bean.getTemporaryFiles( _request );

            assertNotNull( "getTemporaryFiles should return a non-null page", html );
            assertTrue( "The page should display the days-before-delete message when the daemon is activated",
                    html.contains( EXPECTED_MESSAGE_FRAGMENT ) );
            assertTrue( "The message should reflect the configured number of days (42)", html.contains( "42" ) );

            List<TemporaryFile> files = TemporaryFileHome.findByUser( _user );
            assertFalse( "The user's temporary files should still be listed", files.isEmpty( ) );
        }
        finally
        {
            TemporaryFileHome.remove( file.getIdFile( ) );
        }
    }

    public void testGetTemporaryFiles_daemonNotActivated_pageDoesNotContainDaysBeforeDeleteMessage( ) throws AccessDeniedException {

        TemporaryFilesJspBean bean = new TemporaryFilesJspBean( );
        bean.init( _request, TemporaryFilesJspBean.VIEW_TEMP_FILES );

        String html = bean.getTemporaryFiles( _request );

        assertNotNull( "getTemporaryFiles should return a non-null page", html );
        assertFalse( "The page should NOT display the days-before-delete message when the daemon is not activated",
                html.contains( EXPECTED_MESSAGE_FRAGMENT ) );
    }


}