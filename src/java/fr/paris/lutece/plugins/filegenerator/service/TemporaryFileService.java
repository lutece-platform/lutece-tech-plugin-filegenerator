package fr.paris.lutece.plugins.filegenerator.service;

import fr.paris.lutece.plugins.filegenerator.business.TemporaryFile;
import fr.paris.lutece.plugins.filegenerator.business.TemporaryFileHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.file.implementation.LocalDatabaseFileService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class TemporaryFileService
{
    private static final TemporaryFileService INSTANCE = new TemporaryFileService( );
    
    private IFileStoreServiceProvider _fileStoreServiceProvider;
    
    private TemporaryFileService( )
    {
        _fileStoreServiceProvider = FileService.getInstance( ).getFileStoreServiceProvider( AppPropertiesService.getProperty( "temporaryfiles.file.provider.service", LocalDatabaseFileService.FILE_STORE_PROVIDER_NAME ) );
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
