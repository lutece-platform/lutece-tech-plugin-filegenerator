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

import java.util.Locale;

import fr.paris.lutece.portal.service.daemon.AppDaemonService;
import fr.paris.lutece.portal.service.daemon.DaemonEntry;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

public class TemporaryFilesJspBeanTest extends LuteceTestCase
{
    private static final String KEY_ONSTARTUP = "daemon.temporaryfilesDaemon.onstartup";
    private static final String KEY_DAYS_BEFORE_DELETE = "daemon.temporaryfilesDaemon.days.defore.delete";

    // Fragment of the resolved i18n message (filegenerator_messages_fr.properties):
    // "Les fichiers seront conservés {0} jours avant suppression."
    private static final String EXPECTED_MESSAGE_FRAGMENT = "avant suppression";

    @Inject
    private TemporaryFilesJspBean _bean;

    private MockHttpServletRequest _request;
    private String _savedOnStartup;
    private String _savedDaysBeforeDelete;

    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _savedOnStartup = System.getProperty( KEY_ONSTARTUP );
        _savedDaysBeforeDelete = System.getProperty( KEY_DAYS_BEFORE_DELETE );

        AdminUser user = new AdminUser( );
        user.setLocale( Locale.FRENCH );

        _request = new MockHttpServletRequest( );
        AdminUserUtils.registerAdminUserWithRight( _request, user, TemporaryFilesJspBean.VIEW_TEMP_FILES );

        _bean.init( _request, TemporaryFilesJspBean.VIEW_TEMP_FILES );
    }

    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        restoreSystemProperty( KEY_ONSTARTUP, _savedOnStartup );
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

    /**
     * daemon.temporaryfilesDaemon.onstartup=1 -> the rendered page must display the
     * "days before delete" info message.
     */
    @Test
    public void testGetTemporaryFiles_daemonActivated_pageContainsDaysBeforeDeleteMessage( ) throws LuteceInitException {
        AppDaemonService.startDaemon("temporaryfilesDaemon");
        System.setProperty( KEY_DAYS_BEFORE_DELETE, "42" );

        fr.paris.lutece.portal.business.user.AdminUser user = new fr.paris.lutece.portal.business.user.AdminUser( );
        user.setLocale( Locale.FRENCH );

        fr.paris.lutece.plugins.filegenerator.business.TemporaryFile file = new fr.paris.lutece.plugins.filegenerator.business.TemporaryFile( );
        file.setUser( user );
        file.setTitle( "test-file.txt" );
        file.setDescription( "test-file" );
        file.setIdPhysicalFile( "1" );
        fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome.create( file );

        AdminUserUtils.registerAdminUserWithRight( _request, user, TemporaryFilesJspBean.VIEW_TEMP_FILES );

        try
        {
            String html = _bean.getTemporaryFiles( _request );

            org.junit.jupiter.api.Assertions.assertNotNull( html, "getTemporaryFiles should return a non-null page" );
            assertTrue( html.contains( EXPECTED_MESSAGE_FRAGMENT ),
                    "The page should display the days-before-delete message when the daemon is activated" );
            assertTrue( html.contains( "42" ), "The message should reflect the configured number of days (42)" );
        }
        finally
        {
            fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome.remove( file.getIdFile( ) );
        }
    }

    /**
     * daemon.temporaryfilesDaemon.onstartup=0 (or any value different from "1") -> the rendered page
     * must NOT display the "days before delete" info message, regardless of what's configured in the
     * .properties file on disk.
     */
    @Test
    public void testGetTemporaryFiles_daemonNotActivated_pageDoesNotContainDaysBeforeDeleteMessage( )
    {
        System.setProperty( KEY_ONSTARTUP, "0" );

        String html = _bean.getTemporaryFiles( _request );

        org.junit.jupiter.api.Assertions.assertNotNull( html, "getTemporaryFiles should return a non-null page" );
        assertFalse( html.contains( EXPECTED_MESSAGE_FRAGMENT ),
                "The page should NOT display the days-before-delete message when the daemon is not activated" );
    }

}