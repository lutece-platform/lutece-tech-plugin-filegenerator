/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.filegenerator.service.download;

import java.util.Map;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.filegenerator.business.TemporaryFile;
import fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileRBACService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named( "filegenerator.temporaryfileRBACService" )
public class TemporaryFileRBACService implements IFileRBACService
{

    private static final long serialVersionUID = 2223704696251378416L;
    private static final String MESSAGE_FILE_ACCESS_DENIED = "Access Denied to this file";
    
    @Override
    public void checkAccessRights( Map<String, String> fileData, User user ) throws AccessDeniedException, UserNotSignedException
    {
        String resourceId = fileData.get( FileService.PARAMETER_RESOURCE_ID );

        if ( null != user )
        {
            AdminUser adminUser = (AdminUser) user;
            TemporaryFile file = TemporaryFileHome.findByPrimaryKey( Integer.valueOf( resourceId ) );
            if ( file.getUser( ).getUserId( ) != adminUser.getUserId( ) )
            {
                throw new AccessDeniedException( MESSAGE_FILE_ACCESS_DENIED );
            }
        }
        else
        {
            throw new AccessDeniedException( MESSAGE_FILE_ACCESS_DENIED );
        }
    }

}
