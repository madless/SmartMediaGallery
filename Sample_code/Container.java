package com.wb.vapps.container;

import android.content.Context;

import com.wb.vapps.dao.container.DAOContainer;
import com.wb.vapps.database.DatabaseManager;
import com.wb.vapps.encryption.factory.MediaEncrypterFactory;
import com.wb.vapps.hardware.PowerManagerVapps;
import com.wb.vapps.localization.Localizer;
import com.wb.vapps.media.video.agents.ExternalDrmLibraryAgentStub;
import com.wb.vapps.media.video.agents.IExternalDrmLibraryAgent;
import com.wb.vapps.media.video.agents.WidevineExternalDrmLibraryAgent;
import com.wb.vapps.media.video.factory.WidevineVideoActivityFactory;
import com.wb.vapps.model.extras.factory.ExtrasModelFactory;
import com.wb.vapps.mvc.views.common.manager.FontManager;
import com.wb.vapps.mvc.views.common.manager.ViewsManager;
import com.wb.vapps.mvc.views.impl.listen.ListenAudioBarHandler;
import com.wb.vapps.network.communication.ConnectionManager;
import com.wb.vapps.network.communication.ConnectionManagerAndroid;
import com.wb.vapps.network.communication.RemoteHostReachabilityChecker;
import com.wb.vapps.network.communication.factory.CommunicationServiceFactory;
import com.wb.vapps.network.synch.ApplicationSynchronizer;
import com.wb.vapps.network.synch.factory.RequestsFactory;
import com.wb.vapps.network.synch.factory.impl.RequestFactoryForFTPSynch;
import com.wb.vapps.network.synch.queue.WindowingSynchronizationQueue;
import com.wb.vapps.operations.factory.OperationsFactory;
import com.wb.vapps.operations.factory.OperationsFactoryDefault;
import com.wb.vapps.operations.factory.OperationsFactoryMock;
import com.wb.vapps.policies.MovieSourcePolicy;
import com.wb.vapps.policies.ProjectTypeBehaviorPolicy;
import com.wb.vapps.policies.PurchaseModePolicy;
import com.wb.vapps.policies.ResourceUrlPolicy;
import com.wb.vapps.policies.impl.MovieSourcePolicyFranchise;
import com.wb.vapps.policies.impl.ProjectTypeBehaviorPolicyDefault;
import com.wb.vapps.policies.impl.PurchaseModePolicyDefault;
import com.wb.vapps.policies.impl.ResourceUrlPolicyFranchise;
import com.wb.vapps.resources.ConfigurationManager;
import com.wb.vapps.resources.ResourcesManager;
import com.wb.vapps.resources.queue.container.MediaQueuesContainer;
import com.wb.vapps.services.languages.LanguagesSwitcher;
import com.wb.vapps.services.launcher.ApplicationLauncherFranchise;
import com.wb.vapps.services.launcher.IApplicationLauncher;
import com.wb.vapps.services.loader.MovieDownloadController;
import com.wb.vapps.services.loader.MovieDownloadPersistentObserver;
import com.wb.vapps.services.loader.MovieDownloadService;
import com.wb.vapps.services.model.ApplicationModelsManager;
import com.wb.vapps.services.model.IntegratedModelsManager;
import com.wb.vapps.services.movie.MovieSourceProvider;
import com.wb.vapps.services.movie.VideoLauncher;
import com.wb.vapps.services.omniture.GoogleAnalyticsTrackerService;
import com.wb.vapps.services.omniture.TrackerService;
import com.wb.vapps.services.preferences.PreferencesManager;
import com.wb.vapps.services.purchase.PurchaseService;
import com.wb.vapps.services.purchase.impl.PurchaseServiceAndroid;
import com.wb.vapps.services.purchase.impl.PurchaseServiceStub;
import com.wb.vapps.services.purchase.persistent.PurchaseServicePersistentObsever;
import com.wb.vapps.services.synch.SynchronizationService;
import com.wb.vapps.settings.Constants;
import com.wb.vapps.settings.Constants.SynchronizationType;
import com.wb.vapps.settings.ProjectConfig;

/**
 * Implements IOCContainer pattern. Singleton object.
 * @author amaximenko
 *
 */
public class Container {
	private static Container instance;	
	
	private Context context;
	
	private Creator creator;
	
	//Database
	private DatabaseManager dbManager;
	
	//DAO
	private DAOContainer daoContainer;
	
	//Networking
	private ConnectionManager connectionManager;
	private CommunicationServiceFactory communicationServiceFactory;
	
	private RemoteHostReachabilityChecker remoteHostReachabilityChecker;
	
	private MovieDownloadService movieDownloadService;
	private MovieDownloadPersistentObserver movieDownloadPersistentObserver;
	
	private ApplicationSynchronizer synchronizer;
	private RequestsFactory requestsFactory;
	
	//Encryption
	private MediaEncrypterFactory mediaEncrypterFactory;
	
	//Model
	private IntegratedModelsManager integratedModelsManager;
	private ApplicationModelsManager applicationModelsManager;
	private ExtrasModelFactory extrasModelFactory;
	
	//Views
	private ViewsManager viewsManager;
	
	//Variables container
	private VappsVariablesContainer variablesContainer;
	
	//Resources
	private ResourcesManager resourcesManager;
	private ConfigurationManager configurationManager;
	private ResourceUrlPolicy resourceUrlPolicy;
	
	//Media queues
	private MediaQueuesContainer mediaQueuesContainer;	
	
	//Policies
	private PurchaseModePolicy purchaseModePolicy;
	private ProjectTypeBehaviorPolicy projectTypeBehaviorPolicy;
	private MovieSourcePolicy movieSourcePolicy;
	
	//Operations
	private OperationsFactory operationsFactory;
	
	//Services
	private MovieSourceProvider movieSourceProvider;
	private VideoLauncher videoLauncher;
	private PreferencesManager preferencesManager;
	private Localizer localizer;
	private IApplicationLauncher applicationLauncher;
	private LanguagesSwitcher languagesSwitcher;
	private SynchronizationService synchronizationService;
	private WindowingSynchronizationQueue windowingSynchronizationQueue;
	private TrackerService trackerService;
	
	private MovieDownloadController movieDownloadController;
	
	//Hardware
	private PowerManagerVapps powerManager;
	
	//Fonts
	private FontManager fontManager;
	
	//Purchase
	private PurchaseService purchaseService;
	private PurchaseServicePersistentObsever purchasePersistentObserver;
	
	//Listen
	private ListenAudioBarHandler listenAudioBarHandler;
	
	//Widevine video factory
	private WidevineVideoActivityFactory widevineVideoActivityFactory;
	private IExternalDrmLibraryAgent widevineExternalDrmLibraryAgent;
		
	private Container() {}
	
	public static Container getInstance() { 
		if (instance == null) {
			instance = new Container();
		}
		
		return instance;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public DatabaseManager getDbManager() {
		if (dbManager == null) {
			dbManager = creator.createDatabaseManager();
		}
		
		return dbManager;
	}
	
	

	public DAOContainer getDaoContainer() {
		if (daoContainer == null) {
			daoContainer = creator.createDaoContainer(getDbManager());
		}
		
		return daoContainer;
	}

	public ConnectionManager getConnectionManager() {
		if (connectionManager == null) {
			connectionManager = creator.createConnectionManager();
			
			if (connectionManager instanceof ConnectionManagerAndroid) {
				((ConnectionManagerAndroid)connectionManager).setRemoteHostReachabilityChecker(getRemoteHostReachabilityChecker());
			}
		}
		
		return connectionManager;
	}

	public CommunicationServiceFactory getCommunicationServiceFactory() {
		if (communicationServiceFactory == null) {
			communicationServiceFactory = creator.createCommunicationServiceFactory();
		}
		
		return communicationServiceFactory;
	}

	public MediaEncrypterFactory getMediaEncrypterFactory() {
		if (Constants.ENCRYPTION_MEDIA_FILES_ENABLED) {
			if (mediaEncrypterFactory == null) {
				mediaEncrypterFactory = creator.greateMediaEncrypterFactory();
			}
		}
		
		return mediaEncrypterFactory;
	}

	public IntegratedModelsManager getIntegratedModelsManager() {
		if (integratedModelsManager == null) {
			integratedModelsManager = creator.createIntegratedModelsManager();
			integratedModelsManager.setDaoContainer(getDaoContainer());
		}
		
		return integratedModelsManager;
	}	

	public ApplicationModelsManager getApplicationModelsManager() {
		if (applicationModelsManager == null) {
			applicationModelsManager = creator.createApplicationModelsManager();
			applicationModelsManager.setDaoContainer(getDaoContainer());
			applicationModelsManager.setIntegratedModelsManager(getIntegratedModelsManager());
			applicationModelsManager.setResourcesManager(getResourcesManager());
			applicationModelsManager.setMovieSourceProvider(getMovieSourceProvider());
			applicationModelsManager.setLocalizer(getLocalizer());
		}
		
		return applicationModelsManager;
	}

	public ExtrasModelFactory getExtrasModelFactory() {
		if (extrasModelFactory == null) {
			extrasModelFactory = new ExtrasModelFactory();
		}
		
		return extrasModelFactory;
	}

	public ViewsManager getViewsManager() {
		if (viewsManager == null) {
			viewsManager = creator.createViewsManager(getConfigurationManager());
		}
		
		return viewsManager;
	}

	public VappsVariablesContainer getVariablesContainer() {
		if (variablesContainer == null) {
			variablesContainer = new VappsVariablesContainer();
		}
		
		return variablesContainer;
	}

	public ResourcesManager getResourcesManager() {
		if (resourcesManager == null) {
			resourcesManager = new ResourcesManager();
			resourcesManager.setVariablesContainer(getVariablesContainer());
			resourcesManager.setMediaEncrypterFactory(getMediaEncrypterFactory());
			resourcesManager.setContext(getContext());
			resourcesManager.setResourceUrlPolicy(getResourceUrlPolicy());
		}
		
		return resourcesManager;
	}
	
	public ConfigurationManager getConfigurationManager() {
		if(configurationManager == null) {
			configurationManager = new ConfigurationManager(getContext());
		}
		
		return configurationManager;
	}
	
	public ResourceUrlPolicy getResourceUrlPolicy() {
		if(resourceUrlPolicy == null) {
			resourceUrlPolicy = new ResourceUrlPolicyFranchise();
			((ResourceUrlPolicyFranchise)resourceUrlPolicy).setConfigurationManager(getConfigurationManager());			
		}
		
		return resourceUrlPolicy;
	}

	public MediaQueuesContainer getMediaQueuesContainer() {
		if (mediaQueuesContainer == null) {
			mediaQueuesContainer = new MediaQueuesContainer();
			mediaQueuesContainer.setResourcesManager(getResourcesManager());			
			mediaQueuesContainer.setCommunicationServiceFactory(getCommunicationServiceFactory());
			mediaQueuesContainer.setMediaEncrypterFactory(getMediaEncrypterFactory());
			
			mediaQueuesContainer.initQueues();
		}
		
		return mediaQueuesContainer;
	}

	public PurchaseModePolicy getPurchaseModePolicy() {
		if (purchaseModePolicy == null) {
			purchaseModePolicy = new PurchaseModePolicyDefault();
			
			((PurchaseModePolicyDefault)purchaseModePolicy).setLocalizer(getLocalizer());
		}
		
		return purchaseModePolicy;
	}

	public OperationsFactory getOperationsFactory() {
		if (operationsFactory == null) {
			if (Constants.USE_MOCK_OPERATIONS) {
				operationsFactory = new OperationsFactoryMock();
				((OperationsFactoryMock)operationsFactory).setContext(getContext());
				((OperationsFactoryMock)operationsFactory).setResourcesManager(getResourcesManager());
			}
			else {
				operationsFactory = new OperationsFactoryDefault();				
				((OperationsFactoryDefault)operationsFactory).setResourcesManager(getResourcesManager());	
				((OperationsFactoryDefault)operationsFactory).setCommunicationServiceFactory(getCommunicationServiceFactory());
			}
		}
		
		return operationsFactory;
	}

	public MovieDownloadService getMovieDownloadService() {
		if (movieDownloadService == null) {
			movieDownloadService = new MovieDownloadService();
			movieDownloadService.setResourcesManager(getResourcesManager());
		}
		
		return movieDownloadService;
	}

	public MovieDownloadPersistentObserver getMovieDownloadPersistentObserver() {
		if (movieDownloadPersistentObserver == null) {
			movieDownloadPersistentObserver = new MovieDownloadPersistentObserver();
			movieDownloadPersistentObserver.setDaoContainer(getDaoContainer());
		}
		
		return movieDownloadPersistentObserver;
	}

	public MovieSourceProvider getMovieSourceProvider() {
		if (movieSourceProvider == null) {
			movieSourceProvider = new MovieSourceProvider();
			movieSourceProvider.setResourcesManager(getResourcesManager());
			movieSourceProvider.setLocalizer(getLocalizer());
			movieSourceProvider.setDaoContainer(getDaoContainer());
			movieSourceProvider.setMovieSourcePolicy(getMovieSourcePolicy());
			movieSourceProvider.setPreferencesManager(getPreferencesManager());			
		}
		
		return movieSourceProvider;
	}

	public PowerManagerVapps getPowerManager() {
		if (powerManager == null) {
			powerManager = new PowerManagerVapps(context);			
		}
		
		return powerManager;
	}
	
	public FontManager getFontManager() {
		if(fontManager == null) {
			fontManager = creator.createFontManager();
		}
		
		return fontManager;
	}

	public PreferencesManager getPreferencesManager() {
		if (preferencesManager == null) {
			preferencesManager = new PreferencesManager(getContext());
			preferencesManager.setPreferencesDAO(getDaoContainer().getPreferencesDAO());
			preferencesManager.setIntegratedModelsManager(getIntegratedModelsManager());
			preferencesManager.setLocalizer(getLocalizer());
		}
		
		return preferencesManager;
	}

	public PurchaseService getPurchaseService() {
		if (purchaseService == null) {
			if (Constants.USE_PURCHASE_STUB_SERVICE) {
				purchaseService = new PurchaseServiceStub();
			}
			else {
				purchaseService = new PurchaseServiceAndroid();
			}
			
			purchaseService.addPurchaseObserver(getPurchasePersistentObserver());
		}
		
		return purchaseService;
	}

	public PurchaseServicePersistentObsever getPurchasePersistentObserver() {
		if (purchasePersistentObserver == null) {
			purchasePersistentObserver = new PurchaseServicePersistentObsever();
		}
		
		return purchasePersistentObserver;
	}

	public Localizer getLocalizer() {
		if (localizer == null) {
			localizer = new Localizer();
			localizer.setIntegratedModelsManager(getIntegratedModelsManager());
			localizer.setResourcesManager(getResourcesManager());			
			localizer.setCommonLocalizationsDAO(getDaoContainer().getCommonLocalizationsDAO());
		}
		
		return localizer;
	}

	public IApplicationLauncher getApplicationLauncher() {
		if (applicationLauncher == null) {
			applicationLauncher = new ApplicationLauncherFranchise();
			
			((ApplicationLauncherFranchise)applicationLauncher).setSynchronizationService(getSynchronizationService());
			((ApplicationLauncherFranchise)applicationLauncher).setMovieDownloadController(getMovieDownloadController());
			((ApplicationLauncherFranchise)applicationLauncher).setApplicationModelsManager(getApplicationModelsManager());
		}
		
		return applicationLauncher;
	}

	public LanguagesSwitcher getLanguagesSwitcher() {
		if (languagesSwitcher == null) {
			languagesSwitcher = new LanguagesSwitcher();
			
			languagesSwitcher.setLocalizer(getLocalizer());
			languagesSwitcher.setPreferencesManager(getPreferencesManager());
			languagesSwitcher.setViewsManager(getViewsManager());
			languagesSwitcher.setMovieDownloadController(getMovieDownloadController());
		}
		
		return languagesSwitcher;
	}

	public ProjectTypeBehaviorPolicy getProjectTypeBehaviorPolicy() {
		if (projectTypeBehaviorPolicy == null) {
			ProjectTypeBehaviorPolicyDefault policy = new ProjectTypeBehaviorPolicyDefault();
			
			policy.setViewsManager(getViewsManager());
			policy.setConfigurationManager(getConfigurationManager());
			
			projectTypeBehaviorPolicy = policy;
		}
		
		return projectTypeBehaviorPolicy;
	}

	public ApplicationSynchronizer getSynchronizer() {
		if (synchronizer == null) {
			synchronizer = new ApplicationSynchronizer();
			
			synchronizer.setRequestsFactory(getRequestsFactory());
			synchronizer.setCommunicationServiceFactory(getCommunicationServiceFactory());
			synchronizer.setIntegratedModelsManager(getIntegratedModelsManager());
			synchronizer.setDaoContainer(getDaoContainer());			
			synchronizer.setDbManager(getDbManager());
			synchronizer.setResourceManager(getResourcesManager());
			synchronizer.setMediaQueuesContainer(getMediaQueuesContainer());
			synchronizer.setResourceUrlPolicy(getResourceUrlPolicy());
		}
		
		return synchronizer;
	}

	public RequestsFactory getRequestsFactory() {
		if (requestsFactory == null) {
			
			if (ProjectConfig.getInstance().getSynchronizationType() == SynchronizationType.SYNCH_TYPE_FTP) {
				requestsFactory = new RequestFactoryForFTPSynch();
			}
		}
		
		return requestsFactory;
	}

	public SynchronizationService getSynchronizationService() {
		if (synchronizationService == null) {
			synchronizationService = new SynchronizationService();
			
			synchronizationService.setSynchronizer(getSynchronizer());
		}
		
		return synchronizationService;
	}

	public MovieDownloadController getMovieDownloadController() {
		if (movieDownloadController == null) {
			movieDownloadController = new MovieDownloadController();
			
			movieDownloadController.setDownloadService(getMovieDownloadService());
			movieDownloadController.setMovieSourceProvider(getMovieSourceProvider());
		}
		
		return movieDownloadController;
	}

	public RemoteHostReachabilityChecker getRemoteHostReachabilityChecker() {
		if (remoteHostReachabilityChecker == null) {
			remoteHostReachabilityChecker = new RemoteHostReachabilityChecker(Constants.REMOTE_HOST_FOR_REACHABILITY_LISTENING);
		}
		
		return remoteHostReachabilityChecker;
	}

	public WindowingSynchronizationQueue getWindowingSynchronizationQueue() {
		if (windowingSynchronizationQueue == null) {
			windowingSynchronizationQueue = new WindowingSynchronizationQueue();
			
			windowingSynchronizationQueue.setCommunicationServiceFactory(getCommunicationServiceFactory());
			windowingSynchronizationQueue.setDaoContainer(getDaoContainer());
			windowingSynchronizationQueue.setRequestsFactory(getRequestsFactory());			
			windowingSynchronizationQueue.setDatabaseManager(getDbManager());
		}
		
		return windowingSynchronizationQueue;
	}

	public ListenAudioBarHandler getListenAudioBarHandler() {
		if (listenAudioBarHandler == null) {
			listenAudioBarHandler = new ListenAudioBarHandler();
			
			listenAudioBarHandler.setResourcesManager(getResourcesManager());
		}
		
		return listenAudioBarHandler;
	}
	
	public static boolean isInitialized() {
		return instance != null && instance.getCreator() != null;
	}

	public TrackerService getTrackerService() {
		if(trackerService == null) {
			trackerService = new GoogleAnalyticsTrackerService();
		}
		return trackerService;
	}

	public WidevineVideoActivityFactory getWidevineVideoActivityFactory() {
		if (widevineVideoActivityFactory == null) {
			widevineVideoActivityFactory = new WidevineVideoActivityFactory();
			
			widevineVideoActivityFactory.setMovieSourcePolicy(getMovieSourcePolicy());
		}
		
		return widevineVideoActivityFactory;
	}

	public IExternalDrmLibraryAgent getWidevineExternalDrmLibraryAgent() {
		if (widevineExternalDrmLibraryAgent == null) {
			
			if (getMovieSourcePolicy().shouldUseNativeDrmLibrary()) {				
				widevineExternalDrmLibraryAgent = new ExternalDrmLibraryAgentStub();
			}
			else {
				widevineExternalDrmLibraryAgent = new WidevineExternalDrmLibraryAgent();
			}
		}
		
		return widevineExternalDrmLibraryAgent;
	}

	public VideoLauncher getVideoLauncher() {
		if (videoLauncher == null) {
			videoLauncher = new VideoLauncher();
			
			videoLauncher.setPreferencesManager(getPreferencesManager());
			videoLauncher.setPurchaseModePolicy(getPurchaseModePolicy());
			videoLauncher.setResourcesManager(getResourcesManager());
			videoLauncher.setWidevineVideoActivityFactory(getWidevineVideoActivityFactory());
			videoLauncher.setLocalizer(getLocalizer());
		}
		
		return videoLauncher;
	}

	public MovieSourcePolicy getMovieSourcePolicy() {
		if (movieSourcePolicy == null) {
			movieSourcePolicy = new MovieSourcePolicyFranchise();
			
			((MovieSourcePolicyFranchise)movieSourcePolicy).setConfigurationManager(getConfigurationManager());
			((MovieSourcePolicyFranchise)movieSourcePolicy).setLocalizer(getLocalizer());
			
		}
		
		return movieSourcePolicy;
	}
	
}
