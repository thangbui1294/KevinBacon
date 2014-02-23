package sdokb.ui;

import sdokb.game.KevinBaconGameStateManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import sdokb.SixDegreesOfKevinBacon.KevinBaconPropertyType;
import sdokb.file.KevinBaconFileUtilities;
import properties_manager.PropertiesManager;
import sdokb.game.Actor;
import sdokb.game.Connection;
import sdokb.game.Film;
import sdokb.game.IMDBObject;
import sdokb.game.KevinBaconGameData;
import sdokb.game.KevinBaconGameGraphManager;

/**
 * This class contains all user interface components and maintains
 * all presentation and interaction objects for running the game.
 * 
 * @author Richard McKenna & _________________
 */
public class KevinBaconUI
{   
    /**
     * The SixDegreesOfKevinBaconUIState represents the four screen states
     * that are possible for the game application. Depending on which
     * state is in current use, different controls will be visible.
     */
    public enum UIState
    {
        SPLASH_SCREEN_STATE,
        PLAY_GAME_STATE,
        VIEW_STATS_STATE,
        VIEW_HELP_STATE
    }
    
    // THIS OBJECT MAINTAINS ALL GAME STATE INFORMATION AND
    // IS COMPLETELY INDEPENDENT OF THE USER INTERFACE
    private KevinBaconGameStateManager gsm;
    
    // THIS IS THE GAME WINDOW, WE'LL PUT ALL UI COMPONENTS IN HERE
    private JFrame window;
  
    // WHEN THE APP STARTS, THE SPLASH SCREEN WILL ASK THE
    // USER TO CHOOSE A LANGUAGE
    private JLabel splashScreenImageLabel;
    private JPanel languageSelectionPanel;
    private ArrayList<JButton> languageButtons;

    // ONCE THE LANGUAGE IS SELECTED, THE APP WILL
    // HAVE THREE DIFFEFENT WORKSPACES, SO WE'LL HAVE
    // ONE PANE FOR EACH ONE. WE'LL THEN SWAP THEM
    // IN AND OUT AS NEEDED ON THE WORKSPACE PANEL
    private JPanel workspace;
    
    // THE TOOLBAR WILL BE AT THE ABOVE THE WORKSPACE
    // TO PROVIDE FOR NAVIGATION BETWEEN THEM
    private JPanel northToolbar;
    private JButton gameButton;
    private JButton statsButton;
    private JButton helpButton;
    private JButton exitButton;

    // THE GAME PANEL WILL HAVE CONTROLS FOR PLAYING THE GAME
    private JEditorPane gamePane;
    private JLabel guessLabel;
    private DefaultComboBoxModel<IMDBObject> guessModel;
    private JComboBox<IMDBObject> guessComboBox;
    private boolean comboAcceptingInput;
    private IMDBObject dummyComboSelection;
    private JButton newGameButton;

    // THE GAME STATS PANEL WILL DISPLAY GAME STATS
    private JScrollPane statsScrollPane;
    private JEditorPane statsPane;
    
    // THE HELP PANEL WILL EXPLAIN HOW TO PLAY THE
    // GAME. THIS WILL BE PRESENTED USING AN HTML PAGE
    private JPanel helpPanel;
    private JScrollPane helpScrollPane;
    private JEditorPane helpPane;   
    private JButton homeButton;
    
    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private KevinBaconEventHandler eventHandler;
    
    // THIS CLASS WILL HANDLE ALL ERRORS FOR THIS PROGRAM
    private KevinBaconErrorHandler errorHandler;
    
    // THIS CLASS WILL BUILD THE HTML WE'LL USE TO DISPLAY INFO
    private KevinBaconDocumentManager docManager;
    
    // WE'LL USE THIS TO INITIALIZE ALL BUTTONS
    private Insets marginlessInsets;
    
    /**
     * Default constructor, it initializes the GUI for use, but does
     * not yet load all the language-dependent controls, that needs
     * to be done via the startUI method after the user has selected
     * a language.
     */
    public KevinBaconUI()
    {        
        // KEEP THE GSM FOR RUNNING THE GAME
        gsm = new KevinBaconGameStateManager(this);

        // WE'LL USE THIS EVENT HANDLER FOR LOTS OF CONTROLS
        eventHandler = new KevinBaconEventHandler(this);
        
        // WE'LL USE THIS ERROR HANDLER WHEN SOMETHING GOES WRONG
        errorHandler = new KevinBaconErrorHandler(window);
        
        // THIS WILL BUILD HTML FOR OUR GAME, STATS, AND HELP PAGES
        docManager = new KevinBaconDocumentManager(this);
    }
   
    // ACCESSOR METHODS
    
    /**
     * Accessor method for getting the game state manager.
     * 
     * @return The game state manager for this game.
     */
    public KevinBaconGameStateManager    getGSM()            {   return gsm;     }
    
    /**
     * Accessor method for getting the window for this game.
     * 
     * @return The window that game is played inside.
     */
    public JFrame                   getWindow()         {   return window;  }
    
    /**
     * Accessor method for getting the error handler for this game.
     * 
     * @return The error handler that provides responses to error conditions.
     */
    public KevinBaconErrorHandler        getErrorHandler()   { return errorHandler;  }
    
    /**
     * Accessor method for getting the document manager.
     * 
     * @return The document manager, which updates HTML for the game application.
     */
    public KevinBaconDocumentManager     getDocManager()     { return docManager; }

    /**
     * Accessor method for getting the helpPane.
     * 
     * @return The help pane, which displays the Help Web page.
     */
    public JEditorPane getHelpPane() 
    { 
        return helpPane; 
    }
    
    public void setComboAcceptingInput(boolean initComboAcceptingInput)
    {
        comboAcceptingInput = initComboAcceptingInput;
    }
    
    /**
     * This function is called after the constructor. It will initialize the window
     * and the splash screen and start up the window, sending the application
     * into event handling mode.
     */
    public void startUI()
    {
        // SETUP THE WINDOW
        initWindow();
        
        // SETUP THE FIRST GUI SCREEN
        initSplashScreen();
        
        // AND OPEN THE WINDOW
        window.setVisible(true);
    }

    // INITIALIZATION METHODS - THE FOLLOWING METHODS SETUP THE USER INTERFACE
    // FOR USE. NOTE THAT THEY ARE ALL PRIVATE SO THAT THEY WON'T TEMPT SOMEONE
    // TO USE THEM AT AN IMPROPER TIME
    // -initWindow
    // -initSplashScreen
    
    /**
     * This function initializes the window for use.
     */
    private void initWindow()
    {
        // WE'RE GOING TO PUT EVERYTHING IN THE WINDOW,
        window = new JFrame();
   
        // ASK THE EVENT HANDLER TO PROVIDE A RESPONSE WHEN THE
        // USER CLICKS THE WINDOW'S TOP-RIGHT 'X'
        window.addWindowListener(new WindowAdapter()
        {   @Override
            public void windowClosing(WindowEvent we)
            {
                eventHandler.respondToExitRequest(window);
            }            
        });
        
        // WE'LL LET THE WINDOW HANDLER DEAL WITH THIS, SEE THE PREVIOUS CODE BLOCK
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // GET THE LOADED TITLE AND SET IT IN THE FRAME
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(KevinBaconPropertyType.SPLASH_SCREEN_TITLE_TEXT);
        window.setTitle(title);
        
        // SET THE WINDOW ICON
        String windowIconFile = props.getProperty(KevinBaconPropertyType.WINDOW_ICON);
        Image windowIcon = loadImage(windowIconFile);
        window.setIconImage(windowIcon);

        // SIZE THE WINDOW VIA XML PROPERTIES AND DON'T LET THE WINDOW BE RESIZED
        int windowWidth = Integer.parseInt(props.getProperty(KevinBaconPropertyType.WINDOW_WIDTH));
        int windowHeight = Integer.parseInt(props.getProperty(KevinBaconPropertyType.WINDOW_HEIGHT));
        window.setSize(windowWidth, windowHeight);
        window.setResizable(false);
    }
    
    /**
     * This function initializes the splash screen for the application,
     * which includes the language selection controls.
     */
    private void initSplashScreen()
    {
        // WE'RE GOING TO USE IMAGES FOR ALL BUTTONS,
        // SO WE'LL SET THIS AS THE MARGIN FOR ALL OF THEM
        marginlessInsets = new Insets(0,0,0,0);

        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props.getProperty(KevinBaconPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        Image splashScreenImage = loadImage(splashScreenImagePath);
        ImageIcon splashIcon = new ImageIcon(splashScreenImage);
        splashScreenImageLabel = new JLabel(splashIcon);
        
        // GET THE LIST OF LANGUAGE OPTIONS
        ArrayList<String> languages = props.getPropertyOptionsList(KevinBaconPropertyType.LANGUAGE_OPTIONS);
        ArrayList<String> languageImages = props.getPropertyOptionsList(KevinBaconPropertyType.LANGUAGE_IMAGE_NAMES);
        
        // NOW, FOR EACH LANGUAGE, ADD A BUTTON
        languageSelectionPanel = new JPanel();
        languageSelectionPanel.setBackground(new Color(189, 254, 0));
        languageButtons = new ArrayList();
        for (int i = 0; i < languages.size(); i++)
        {
            // GET THE LANGUAGE INFORMATION
            String lang = languages.get(i);
            String langImageName = languageImages.get(i);
            Image langImage = loadImage(langImageName);
            ImageIcon langImageIcon = new ImageIcon(langImage);

            // AND BUILD THE BUTTON
            JButton langButton = new JButton(langImageIcon);
            langButton.setActionCommand(lang);
            langButton.setMargin(marginlessInsets);
            languageSelectionPanel.add(langButton);
            
            // CONNECT THE BUTTON TO THE EVENT HANDLER
            langButton.addActionListener(new ActionListener()
            {   @Override
                public void actionPerformed(ActionEvent ae)
                {
                    eventHandler.respondToSelectLanguageRequest(ae.getActionCommand());
                }
            });
        }
        
        // NOW ORGANIZE EVERYTHING INSIDE THE WINDOW
        window.add(splashScreenImageLabel, BorderLayout.CENTER);
        window.add(languageSelectionPanel, BorderLayout.SOUTH);
    }

    // NOTE THAT THE FOLLOWING INIT METHODS ARE CALLED AFTER THE LANGUAGE HAS
    // BEEN CHOSEN BY THE USER. THE initUI IS THE ONLY ONE THAT'S PUBLIC
    // AND IT GETS THE OTHERS ROLLING
    // -initWorkspace
    // -initNorthToolbar
    // -initGameScreen
    // -initStatsPane
    // -initHelpPane    
    
    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initUI()
    {
        // FIRST REMOVE THE SPLASH SCREEN
        window.getContentPane().removeAll();
        
        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(KevinBaconPropertyType.GAME_TITLE_TEXT);
        window.setTitle(title);
        
        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        initNorthToolbar();

        // OUR WORKSPACE WILL STORE EITHER THE GAME, STATS,
        // OR HELP UI AT ANY ONE TIME
        initWorkspace();
        initGameScreen();
        initStatsPane();
        initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        changeWorkspace(UIState.PLAY_GAME_STATE);

        // MAKE SURE THE WINDOW REFLECTS ALL THESE CHANGES IMMEDIATELY
        window.revalidate();
    }     
    
    /**
     * The workspace is a panel that will show different screens
     * depending on the user's requests.
     */
    private void initWorkspace()
    {
        // THE WORKSPACE WILL GO IN THE CENTER OF THE WINDOW, UNDER THE NORTH TOOLBAR
        workspace = new JPanel();
        window.getContentPane().add(workspace, BorderLayout.CENTER);
        
        // THE CardLayout MANAGER LETS US SWITCH EASILY
        CardLayout cardLayout = new CardLayout();
        workspace.setLayout(cardLayout);
    }

    /**
     * This function initializes all the controls that go in 
     * the north toolbar.
     */
    private void initNorthToolbar()
    {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new JPanel();
        northToolbar.setBackground(Color.LIGHT_GRAY);

        // MAKE AND INIT THE GAME BUTTON
        gameButton = initToolbarButton(northToolbar, KevinBaconPropertyType.GAME_IMG_NAME);
        setTooltip(gameButton, KevinBaconPropertyType.GAME_TOOLTIP);
        gameButton.addActionListener(new ActionListener()
            {@Override
            public void actionPerformed(ActionEvent ae)
                {
                    eventHandler.respondToSwitchScreenRequest(UIState.PLAY_GAME_STATE);
                }
            });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar, KevinBaconPropertyType.STATS_IMG_NAME);
        setTooltip(statsButton, KevinBaconPropertyType.STATS_TOOLTIP);
        statsButton.addActionListener(new ActionListener()
            {@Override
             public void actionPerformed(ActionEvent ae)
                {
                    eventHandler.respondToSwitchScreenRequest(UIState.VIEW_STATS_STATE);
                }
            });

        // MAKE AND INIT THE HELP BUTTON
        helpButton = initToolbarButton(northToolbar, KevinBaconPropertyType.HELP_IMG_NAME);
        setTooltip(helpButton, KevinBaconPropertyType.HELP_TOOLTIP);        
        helpButton.addActionListener(new ActionListener()
            {@Override
             public void actionPerformed(ActionEvent ae)
                {
                    eventHandler.respondToSwitchScreenRequest(UIState.VIEW_HELP_STATE);
                }
            });

        // MAKE AND INIT THE EXIT BUTTON
        exitButton = initToolbarButton(northToolbar, KevinBaconPropertyType.EXIT_IMG_NAME);
        setTooltip(exitButton, KevinBaconPropertyType.EXIT_TOOLTIP);
        exitButton.addActionListener(new ActionListener()
            {@Override
             public void actionPerformed(ActionEvent ae)
                {
                    eventHandler.respondToExitRequest(window);
                }
            });
        
        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        window.getContentPane().add(northToolbar, BorderLayout.NORTH);
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     * 
     * @param toolbar The toolbar for which to add the button.
     * 
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     * 
     * @return A constructed button initialized and added to the toolbar.
     */
    private JButton initToolbarButton(JPanel toolbar, KevinBaconPropertyType prop)
    {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);
        
        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageIcon imageIcon = new ImageIcon(image);
        
        // MAKE THE BUTTON
        JButton button = new JButton(imageIcon);
        button.setMargin(marginlessInsets);
        
        // PUT IT IN THE TOOLBAR
        toolbar.add(button);    
        
        // AND SEND BACK THE BUTTON
        return button;
    }

    /**
     * This method initializes the game screen for running the game.
     */
    private void initGameScreen()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
             
        // THE GUESS HISTORY GOES IN THE CENTER, WHICH WE'LL DISPLAY
        // USING HTML IN A JEditorPane
        gamePane = new JEditorPane();
        gamePane.setEditable(false);
        gamePane.setContentType("text/html");

        // LET'S LOAD THE INITIAL HTML INTO THE STATS EDITOR PAGE
        this.loadPage(gamePane, KevinBaconPropertyType.GAME_FILE_NAME);
        HTMLDocument gameDoc = (HTMLDocument)gamePane.getDocument();
        docManager.setGameDoc(gameDoc);
        JScrollPane guessesScrollPane = new JScrollPane(gamePane);
        
        // WE'LL PUT THE GUESSING CONTROLS IN THE SOUTH
        JPanel guessingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guessingPanel.setBackground(Color.LIGHT_GRAY);

        // THE NEW GAME BUTTON IS FIRST CONTROL FOR THE SOUTH
        newGameButton = this.initToolbarButton(guessingPanel, KevinBaconPropertyType.NEW_GAME_IMG_NAME);
        setTooltip(newGameButton, KevinBaconPropertyType.NEW_GAME_TOOLTIP);
        newGameButton.addActionListener(new ActionListener()
            {@Override
             public void actionPerformed(ActionEvent ae)
                {
                    eventHandler.respondToNewGameRequest();
                }
            });

        String guessPromptText = props.getProperty(KevinBaconPropertyType.FILM_LABEL);
        guessLabel = new JLabel(guessPromptText);
        guessingPanel.add(guessLabel);
        guessModel = new DefaultComboBoxModel();
        guessComboBox = new JComboBox(guessModel);
        guessingPanel.add(guessComboBox);
        
        String selectPrompt = props.getProperty(KevinBaconPropertyType.SELECT_LABEL);
        dummyComboSelection = new IMDBObject(selectPrompt);
        setComboAcceptingInput(false);
        
        // RESPONDS WHEN THE USER SELECTS A FILM OR ACTOR
        guessComboBox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent ie)
            {
                IMDBObject selectedItem = (IMDBObject)ie.getItem();
                if (gsm.isGameInProgress() 
                        && comboAcceptingInput
                        && (!selectedItem.equals(dummyComboSelection))
                        && (ie.getStateChange() == ItemEvent.SELECTED))
                {
                    setComboAcceptingInput(false);
                    eventHandler.respondToGuessRequest(selectedItem);
                }
            }    
        });
        
        // THIS SETS UP THE FONTS USED FOR THE COMBO BOX AND LABEL
        String fontGuessFamily = props.getProperty(KevinBaconPropertyType.GUESSES_FONT_FAMILY);
        int fontGuessSize = Integer.parseInt(props.getProperty(KevinBaconPropertyType.GUESSES_FONT_SIZE));
        Font guessFont = new Font(fontGuessFamily, Font.BOLD, fontGuessSize);
        guessLabel.setFont(guessFont);
        guessComboBox.setFont(guessFont);        
        
        // NOW LAY EVERYTHING OUT IN THE GAME PANEL
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(guessesScrollPane, BorderLayout.CENTER);
        gamePanel.add(guessingPanel, BorderLayout.SOUTH);
        
        // NOW MAKE THIS PANEL PART OF THE WORKSPACE, WHICH MEANS WE
        // CAN EASILY SWITCH TO IT AT ANY TIME
        workspace.add(gamePanel, UIState.PLAY_GAME_STATE.toString());
    }
    
    /**
     * This method initializes the stats pane controls for use.
     */
    private void initStatsPane()
    {
        // WE'LL DISPLAY ALL STATS IN A JEditorPane
        statsPane = new JEditorPane();
        statsPane.setEditable(false);
        statsPane.setContentType("text/html");
 
        // LOAD THE STARTING STATS PAGE, WHICH IS JUST AN OUTLINE
        // AND DOESN"T HAVE ANY OF THE STATS, SINCE THOSE WILL 
        // BE DYNAMICALLY ADDED
        loadPage(statsPane, KevinBaconPropertyType.STATS_FILE_NAME);
        HTMLDocument statsDoc = (HTMLDocument)statsPane.getDocument();
            docManager.setStatsDoc(statsDoc);    
        statsScrollPane = new JScrollPane(statsPane);
        
        // NOW ADD IT TO THE WORKSPACE, MEANING WE CAN SWITCH TO IT
        workspace.add(statsScrollPane, UIState.VIEW_STATS_STATE.toString());
    }

    /**
     * This method initializes the help pane and all of its controls.
     */
    private void initHelpPane()
    {
        // WE'LL DISPLAY ALL HELP INFORMATION USING HTML
        helpPane = new JEditorPane();
        helpPane.setEditable(false);
        helpScrollPane = new JScrollPane(helpPane);
                        
        // NOW LOAD THE HELP HTML
        helpPane.setContentType("text/html");
        
        // MAKE THE HELP BUTTON
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String homeImgName = props.getProperty(KevinBaconPropertyType.HOME_IMG_NAME);
        Image homeImg = loadImage(homeImgName);
        homeButton = new JButton(new ImageIcon(homeImg));
        setTooltip(homeButton, KevinBaconPropertyType.HOME_TOOLTIP);
        homeButton.setMargin(marginlessInsets);
        
        // WE'LL PUT THE HOME BUTTON IN A TOOLBAR IN THE NORTH OF THIS SCREEN,
        // UNDER THE NORTH TOOLBAR THAT'S SHARED BETWEEN THE THREE SCREENS
        JPanel helpToolbar = new JPanel();
        helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());
        helpPanel.add(helpToolbar, BorderLayout.NORTH);
        helpPanel.add(helpScrollPane, BorderLayout.CENTER);
        helpToolbar.add(homeButton);
        helpToolbar.setBackground(Color.WHITE);
        
        // LOAD THE HELP PAGE
        loadPage(helpPane, KevinBaconPropertyType.HELP_FILE_NAME);
        
        // LET OUR HELP PAGE GO HOME VIA THE HOME BUTTON
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                eventHandler.respondToGoHomeRequest();
            }
        });
        
        // LET OUR HELP SCREEN JOURNEY AROUND THE WEB VIA HYPERLINK
        HelpHyperlinkListener hhl = new HelpHyperlinkListener(this);
        helpPane.addHyperlinkListener(hhl);        
        
        // ADD IT TO THE WORKSPACE
        workspace.add(helpPanel, UIState.VIEW_HELP_STATE.toString());
    }        
    
    // ADDITIONAL HELPER METHODS - THESE HELP INIT METHOD IN PARTICULAR
    // TO INITIALIZE THE UI COMPONENTS
    // -loadImage
    // -resetLetterButtonColors
    // -setTooltip

    /**
     * This function loads an image for use. Note that it makes
     * use of the KevinBaconFileUtilities class to do so.
     * 
     * @param imageName The image to load.
     * 
     * @return A fully loaded, ready to use image. Note that if the
     * imageName provided is not valid, we'll let the error handler
     * provide some feedback to the user and this method will
     * return an empty image.
     */
    private Image loadImage(String imageName)
    {
        try
        {
            Image imageToLoad = KevinBaconFileUtilities.loadImage(imageName, window);
            return imageToLoad;
        }
        catch(IOException ioe)
        {
            errorHandler.processError(KevinBaconPropertyType.IMAGE_LOADING_ERROR_TEXT);
        }
        // THIS IS A DUMMY IMAGE THAT WE'LL RETURN SO THAT THE
        // UI CAN USE IT AS A STANDING
        return new BufferedImage(0,0,0);
    }

    /**
     * A tooltip is mouse-over text for a control. This method sets
     * the tooltip for the button argument using the language-specific
     * properties represented by tooltip.
     * 
     * @param button The button whose tooltip we wish to set.
     * 
     * @param tooltip The text to set as the tooltip.
     */
    private void setTooltip(JButton button, KevinBaconPropertyType tooltip)
    {
        // GET THE TEXT AND SET IT AS THE TOOLITP
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String tooltipText = props.getProperty(tooltip);
        button.setToolTipText(tooltipText);
    }

    // THE REST OF THE FUNCTIONS ARE UPDATE FUNCTIONS THAT ARE CALLED
    // FROM OTHER CLASSES IN RESPONSE TO EVENTS. THEY ALL CHANGE 
    // THE UI DISPLAY IN ONE WAY OR ANOTHER
    
    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     * 
     * @param uiScreen The screen to be switched to.
     */
    public void changeWorkspace(UIState uiScreen)
    {
        // SWITCH TO THE REQUESTED SCREEN
        CardLayout workspaceCardLayout = (CardLayout)workspace.getLayout();
        workspaceCardLayout.show(workspace, uiScreen.toString());
    }
    
    /**
     * This method loads the HTML page that corresponds to the fileProperty
     * argument and puts it into the jep argument for display.
     * 
     * @param jep The pane that will display the loaded HTML.
     * 
     * @param fileProperty The file property, whose name can then be
     * retrieved from the property manager.
     */
    public void loadPage(  JEditorPane jep,
                           KevinBaconPropertyType fileProperty)
    {
        // GET THE FILE NAME
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String fileName = props.getProperty(fileProperty);
        try
        {
            // LOAD THE HTML INTO THE EDITOR PANE
            String fileHTML = KevinBaconFileUtilities.loadTextFile(fileName);
            jep.setText(fileHTML);        
        }
        catch(IOException ioe)
        {
            errorHandler.processError(KevinBaconPropertyType.INVALID_URL_ERROR_TEXT);
        }
    }

    /**
     * This method loads the link Web Page into the Help Screen's
     * editor pane.
     * 
     * @param link The Web Page to load.
     */
    public void loadRemoteHelpPage(URL link)
    {
        try
        {
            // PUT THE WEB PAGE IN THE HELP PANE
            Document doc = helpPane.getDocument();
            doc.putProperty(Document.StreamDescriptionProperty, null);
            helpPane.setPage(link);            
        }
        catch(IOException ioe)
        {
            errorHandler.processError(KevinBaconPropertyType.INVALID_URL_ERROR_TEXT);
        }
    }
    
    /**
     * This function clears the UI when a new game is started, resetting the
     * letter color buttons and clearing the guesses display.
     */
    public void resetUI()
    {
        docManager.clearGamePage();
        guessComboBox.setEnabled(true);
    }
    
    /**
     * Gets and returns the films to be loaded into the combo box.
     */
    public ArrayList<IMDBObject> getFilmsForComboBox(ArrayList<String> filmIds)
    {
        // AND NOW GET THE ACTOR'S FILMS
        KevinBaconGameGraphManager graph = gsm.getGameGraphManager();
        ArrayList<IMDBObject> films = new ArrayList();
        Iterator<String> it = filmIds.iterator();
        while (it.hasNext())
        {
            String filmID = it.next();
            Film filmToPutInComboBox = graph.getFilm(filmID);
            films.add(filmToPutInComboBox);
        }
        Collections.sort(films);
        return films;
    }

    /**
     * Gets and returns the actors to be loaded into the combo box.
     */
    public ArrayList<IMDBObject> getActorsForComboBox(ArrayList<String> actorIds)
    {        
        // AND NOW GET THE ACTOR'S FILMS
        KevinBaconGameGraphManager graph = gsm.getGameGraphManager();
        ArrayList<IMDBObject> actors = new ArrayList();
        Iterator<String> it = actorIds.iterator();
        while (it.hasNext())
        {
            String actorID = it.next();
            Actor actorToPutInComboBox = graph.getActor(actorID);
            actors.add(actorToPutInComboBox);
        }
        Collections.sort(actors);
        return actors;
    }

    /**
     * Reloads the combo box with the items that are currently
     * selected according to game progress.
     */
    public void reloadComboBox(ArrayList<String> ids)
    {
        // FIRST CLEAR THE COMBO BOX
        guessModel = new DefaultComboBoxModel();

        // ALWAYS PUT THE DUMMY FIRST
        guessModel.addElement(dummyComboSelection);

        // NOW WE'LL EITHER ADD FILMS OR ACTORS
        KevinBaconGameData gameInProgress = gsm.getGameInProgress();
        Connection lastNode = gameInProgress.getLastConnection();
        KevinBaconGameGraphManager graph = gsm.getGameGraphManager();
        ArrayList<IMDBObject> sortedIMDBObjects;
        String guessLabelText;
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        if (gameInProgress.isWaitingForFilm())
        {
            sortedIMDBObjects = getFilmsForComboBox(ids);
            guessLabelText = props.getProperty(KevinBaconPropertyType.FILM_LABEL);
        }
        else
        {
            sortedIMDBObjects = getActorsForComboBox(ids);
            guessLabelText = props.getProperty(KevinBaconPropertyType.ACTOR_LABEL);
        }
       
        // AND PUT THEM IN THE MODEL
        Iterator<IMDBObject> it = sortedIMDBObjects.iterator();
        while (it.hasNext())
        {
            IMDBObject imdb = it.next();
            guessModel.addElement(imdb);
        }        
        
        // AND LOAD THE MODEL
        guessLabel.setText(guessLabelText);
        setComboAcceptingInput(true);
        guessComboBox.setModel(guessModel);        
    }

    /**
     * Enables/disables the combo box. This component should be disabled
     * when it cannot be used, like when a game is not in progress.
     */
    public void enableGuessComboBox(boolean enabled)
    {
        guessComboBox.setEnabled(enabled);
    }
}