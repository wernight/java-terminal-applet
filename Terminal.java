//******************************************************************************
// Terminal.java:	Applet
// Copyright (c) 1999 WBC
// www.alc.net/wbc
// wbc@alc.net
//
// L'auteur détient et gardes les copyright.
// Vous avez le droit de copier, modifier et compiler cet applet, mais vous devez
// demander l'autorisation à l'auteur pour le vendre ou vendre ses copies modifiés
// ou non. Vous devez aussi laisser le nom, l'url du site et l'e-mail de l'auteur
// dans les sources et dans l'applet compilé.
//
// Desciption: Affiche un texte à la façon d'un terminal.
//******************************************************************************
import java.applet.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

//==============================================================================
// Classe Main de l'applet Terminal
//
//==============================================================================
public class Terminal extends Applet implements Runnable
{
	private boolean bBusy = false, drawBlinkCursor = false, bCursor = false;
	private int nChar, nLigne, nLigneMax = 0;
	private int iWidth, iHeight;
	private Font font;
	private Graphics offScrGC;
	private Image offScrImage;
	private AudioClip acBeep;
	private Image BgImg;

	// PRISE EN CHARGE THREAD:
	//		m_Terminal	est l'objet Thread de l'applet
	//--------------------------------------------------------------------------
	private Thread	 m_Terminal = null;

	// PRISE EN CHARGE PARAMÈTRE:
	//		Les paramètres permettent à un auteur HTML de passer des informations à l'applet;
	// l'auteur HTML les indique à l'aide de la balise <PARAM> à l'intérieur de la balise <APPLET>
	// Les variables suivantes servent à stocker les valeurs des
	// paramètres.
    //--------------------------------------------------------------------------

    // Membres des paramètres de l'applet
    // <type>       <VarMembre>    = <Valeur par défaut>
    //--------------------------------------------------------------------------
	private Color BgColor = Color.black;
	private Color FgColor = Color.green;
	private String m_strLigne[] = new String[300];
	private String m_strBgImg = "Terminal.jpg";

    // Noms des paramètres. Pour modifier le nom d'un paramètre,  il suffit d'apporter
	// une seule modification à la valeur de la chaîne de paramètres ci-dessous.
    //--------------------------------------------------------------------------
	private final String PARAM_BgColor = "BgColor";
	private final String PARAM_FgColor = "FgColor";
	private final String PARAM_File = "MsgFile";
	private final String PARAM_BgImg = "BgImg";
	
	// Constructeur de classes Terminal
	//--------------------------------------------------------------------------
	public Terminal()
	{
		//  TODO: Ajoutez ici les lignes de code supplémentaires du constructeur
	}

	// PRISE EN CHARGE INFO APPLET:
	//		La méthode getAppletInfo() retourne une chaîne de caractères indiquant l'auteur de
	// l'applet,  la date du copyright,  ou des informations diverses.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Nom : Terminal\r\n" +
		       "Auteur: Werner BEROUX\r\n" +
		       "            www.alc.net/wbc/\r\n" +
		       "            wbc@alc.net\r\n" +
		       "Créé avec Microsoft Visual J++ Version 1.1";
	}

	// PRISE EN CHARGE PARAMÈTRE
	//		La méthode getParameterInfo() retourne un tableau de chaînes décrivant
	// les paramètres reconnus par cette applet.
	//
    // Informations de paramètre Terminal:
    //  { "Nom", "Type", "Description" },
    //--------------------------------------------------------------------------
	public String[][] getParameterInfo()
	{
		String[][] info =
		{
			{ PARAM_BgColor, "String", "Couleur de fond en hexa." },
			{ PARAM_FgColor, "String", "Couleur du texte en hexa." },
			{ PARAM_File, "String", "Fichier texte contenant le message." },
			{ PARAM_BgImg, "String", "Image de fond." }
		};
		return info;		
	}

	// La méthode init() est appelée par AWT lorsqu'une applet est chargée pour la première fois ou
	// rechargée. Redéfinissez cette méthode pour effectuer l'initialisation nécessaire à votre
	// applet,  telle que l'initialisation des structures de données,   le chargement d'images ou
	// de polices,  la création de fenêtres indépendantes,  la configuration du gestionnaire de présentation,  ou l'ajout de
	// composants de l'interface utilisateur.
    //--------------------------------------------------------------------------
	public void init()
	{
		// PRISE EN CHARGE PARAMÈTRE
		//		Le code suivant permet de récupérer la valeur de chaque paramètre
		// indiquée par la balise <PARAM> et de la stocker dans une variable
		// de membre.
		//----------------------------------------------------------------------
		String param;
		int i = 0;

		// BgColor: Couleur de fond en hexa.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_BgColor);
		if (param != null)
			BgColor = new Color(Integer.parseInt(param, 16));

		// FgColor: Couleur du texte en hexa.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_FgColor);
		if (param != null)
			FgColor = new Color(Integer.parseInt(param, 16));

        // Charge le message
		//----------------------------------------------------------------------
		param = getParameter(PARAM_File);
		if (param != null)
		{
			// Ouvre le fichier
			StringBuffer stringbuffer = new StringBuffer();
			try
			{
				URL url = new URL(getCodeBase(), param);
				DataInputStream datainputstream = new DataInputStream(url.openStream());
				String str;
				// Charge chaque ligne
				while((str = datainputstream.readLine()) != null) 
					m_strLigne[nLigneMax++] = str;
			}
			catch(IOException _ex)
			{
				System.out.println("Le nom du fichier pour le message est incorrect.");
			}
		}

		// BgImg: Image de fond.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_BgImg);
		if (param != null)
			m_strBgImg = param;

		// Fonte
		font = new Font("Courier", Font.BOLD, 12);

		// Taille de l'applet
		iWidth = size().width;
		iHeight = size().height;

		// Change la couleur du fond
		setBackground(BgColor);

		// Écran off
		offScrImage	= createImage(iWidth, iHeight);
		offScrGC	= offScrImage.getGraphics();

		// Son des touches
//		acBeep = getAudioClip(getCodeBase(), "beep.au");

		// Charge l'image de fond
        MediaTracker mediatracker = new MediaTracker(this);
		BgImg = getImage(getCodeBase(), m_strBgImg);
        mediatracker.addImage(BgImg, 0);
        showStatus("Loading background image");
        try
        {
            mediatracker.waitForAll();
        }
        catch(InterruptedException _ex) { }
        showStatus("Finished loading background image");
	}

	// Insérez ici des lignes de code supplémentaires de l'applet destinées à quitter proprement le système. La méthode destroy() est appelée
	// lorsque votre applet se termine et est déchargée.
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// TODO: Insérez ici le code de l'applet destiné à quitter proprement le système
	}

	public void repaint()
	{
		update(getGraphics());
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	// Gestionnaire de dessin Terminal
	//--------------------------------------------------------------------------
	public void paint(Graphics g)
	{
		int i;
		int LineHeight = 18;
		int YPos;
		int nStartLine;
		String strCurseur;

		if (bBusy)
			return;
		bBusy = true;

		// Curseur ou pas ?
		if (drawBlinkCursor)
		{
			bCursor = !bCursor;
			if (bCursor)
				strCurseur = "|";
			else
				strCurseur = " ";
		}
		else
		{
			if (nChar%4 != 0)
				strCurseur = "|";
			else
				strCurseur = " ";
		}

		// Change la fonte & couleur
		offScrGC.setColor(FgColor);
		offScrGC.setFont(font);

		// Place l'img de fond
		offScrGC.drawImage(BgImg, 0, 0, this);

		// Line de début (permet de monter le texte s'il ne tient pas sur une page
		nStartLine = 0;
		for (i=0; i<=nLigne; i++)
			if ((128+i*LineHeight) > 325)
				nStartLine++;

		// Affiche tout le texte dans l'écran off
		for (YPos=(128+LineHeight*(nLigne-nStartLine)), i=0; i<=nLigne-nStartLine; YPos-=LineHeight, i++)
		{
			// Affiche sur l'écran off
			if (i == 0)
				offScrGC.drawString(m_strLigne[nLigne-i].substring(0,nChar) + strCurseur, 147, YPos);
			else
				offScrGC.drawString(m_strLigne[nLigne-i], 147, YPos);
		}

		// Affiche à l'écran
		g.drawImage(offScrImage, 0, 0, this);
		bBusy = false;
	}

	//		La méthode start() est appelée lorsque la page contenant l'applet
	// s'affiche en premier à l'écran. L'implémentation initiale de l'Assistant Applet
	// pour cette méthode démarre l'exécution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void start()
	{
		if (m_Terminal == null)
		{
			m_Terminal = new Thread(this);
			m_Terminal.start();
		}
		// TODO: Insérez ici des lignes de code supplémentaires pour le démarrage de l'applet
	}
	
	//		La méthode stop() est appelée lorsque la page contenant l'applet
	// disparaît de l'écran. L'implémentation initiale de l'Assistant Applet
	// pour cette méthode arrête l'exécution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void stop()
	{
		if (m_Terminal != null)
		{
			m_Terminal.stop();
			m_Terminal = null;
		}

		// TODO: Insérez ici des lignes de code supplémentaires destinées à arrêter l'applet
	}

	// PRISE EN CHARGE THREAD
	//		La méthode run() est appelée lorsque la thread de l'applet est démarrée.
	// Si votre applet effectue des activités permanentes sans attendre la saisie de données par l'utilisateur
	// le code implémentant ce comportement s'insère en règle générale ici. Par
	// exemple,  pour une applet qui réalise une animation,  la méthode run() gère
	// l'affichage des images.
	//--------------------------------------------------------------------------
	public void run()
	{
		int i;
		int nLigneSuivante;

		while (true)
		{
			try
			{
				// Fin de ligne OU fin du texte
				if (nChar == m_strLigne[nLigne].length())
				{
					// Saute les lignes vides
					nLigneSuivante = nLigne;
					do
						nLigneSuivante++;
					while (nLigneSuivante < nLigneMax && m_strLigne[nLigneSuivante].equals(""));

					// Fin du texte
					if (nLigneSuivante >= nLigneMax)
					{
						drawBlinkCursor = true;
						for (i=0; i<14; i++)
						{
							// Affiche un curseur clignottant.
							Thread.sleep( 500 );
							repaint();
						}
						drawBlinkCursor = false;
						nLigne = 0;
					}
					else // Fin de la ligne
					{
						// Attend si il y avait une ligne vide
						drawBlinkCursor = true;
						if ( m_strLigne[nLigneSuivante-1].equals("") )
							for (i=0; i<(3*Math.random()+4); i++)
							{
								// Affiche un curseur clignottant.
								Thread.sleep( 300 );
								repaint();
							}
						drawBlinkCursor = false;

						// Ligne suivante
						nLigne = nLigneSuivante;
					}
					nChar = 1;
				}
				else // Caractère suivant
				{
					Thread.sleep( (int) (100*Math.random()) );
					nChar++;
				}

				// Son des touches
//				if (acBeep != null)
//					acBeep.play();

				// Demande à afficher
				repaint();
			}
			catch (InterruptedException e)
			{
				// TODO: Insérez ici les lignes de code destinées à traiter les exceptions au cas où
				//       InterruptedException levée par Thread.sleep(),
				//		 ce qui signifie qu'une autre thread a interrompu celle-ci
				stop();
			}
		}
	}



	// TODO: Insérez ici des lignes de code supplémentaires pour l'applet

}
