//******************************************************************************
// Terminal.java:	Applet
// Copyright (c) 1999 WBC
// www.alc.net/wbc
// wbc@alc.net
//
// L'auteur d�tient et gardes les copyright.
// Vous avez le droit de copier, modifier et compiler cet applet, mais vous devez
// demander l'autorisation � l'auteur pour le vendre ou vendre ses copies modifi�s
// ou non. Vous devez aussi laisser le nom, l'url du site et l'e-mail de l'auteur
// dans les sources et dans l'applet compil�.
//
// Desciption: Affiche un texte � la fa�on d'un terminal.
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

	// PRISE EN CHARGE PARAM�TRE:
	//		Les param�tres permettent � un auteur HTML de passer des informations � l'applet;
	// l'auteur HTML les indique � l'aide de la balise <PARAM> � l'int�rieur de la balise <APPLET>
	// Les variables suivantes servent � stocker les valeurs des
	// param�tres.
    //--------------------------------------------------------------------------

    // Membres des param�tres de l'applet
    // <type>       <VarMembre>    = <Valeur par d�faut>
    //--------------------------------------------------------------------------
	private Color BgColor = Color.black;
	private Color FgColor = Color.green;
	private String m_strLigne[] = new String[300];
	private String m_strBgImg = "Terminal.jpg";

    // Noms des param�tres. Pour modifier le nom d'un param�tre,  il suffit d'apporter
	// une seule modification � la valeur de la cha�ne de param�tres ci-dessous.
    //--------------------------------------------------------------------------
	private final String PARAM_BgColor = "BgColor";
	private final String PARAM_FgColor = "FgColor";
	private final String PARAM_File = "MsgFile";
	private final String PARAM_BgImg = "BgImg";
	
	// Constructeur de classes Terminal
	//--------------------------------------------------------------------------
	public Terminal()
	{
		//  TODO: Ajoutez ici les lignes de code suppl�mentaires du constructeur
	}

	// PRISE EN CHARGE INFO APPLET:
	//		La m�thode getAppletInfo() retourne une cha�ne de caract�res indiquant l'auteur de
	// l'applet,  la date du copyright,  ou des informations diverses.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Nom : Terminal\r\n" +
		       "Auteur: Werner BEROUX\r\n" +
		       "            www.alc.net/wbc/\r\n" +
		       "            wbc@alc.net\r\n" +
		       "Cr�� avec Microsoft Visual J++ Version 1.1";
	}

	// PRISE EN CHARGE PARAM�TRE
	//		La m�thode getParameterInfo() retourne un tableau de cha�nes d�crivant
	// les param�tres reconnus par cette applet.
	//
    // Informations de param�tre Terminal:
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

	// La m�thode init() est appel�e par AWT lorsqu'une applet est charg�e pour la premi�re fois ou
	// recharg�e. Red�finissez cette m�thode pour effectuer l'initialisation n�cessaire � votre
	// applet,  telle que l'initialisation des structures de donn�es,   le chargement d'images ou
	// de polices,  la cr�ation de fen�tres ind�pendantes,  la configuration du gestionnaire de pr�sentation,  ou l'ajout de
	// composants de l'interface utilisateur.
    //--------------------------------------------------------------------------
	public void init()
	{
		// PRISE EN CHARGE PARAM�TRE
		//		Le code suivant permet de r�cup�rer la valeur de chaque param�tre
		// indiqu�e par la balise <PARAM> et de la stocker dans une variable
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

		// �cran off
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

	// Ins�rez ici des lignes de code suppl�mentaires de l'applet destin�es � quitter proprement le syst�me. La m�thode destroy() est appel�e
	// lorsque votre applet se termine et est d�charg�e.
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// TODO: Ins�rez ici le code de l'applet destin� � quitter proprement le syst�me
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

		// Line de d�but (permet de monter le texte s'il ne tient pas sur une page
		nStartLine = 0;
		for (i=0; i<=nLigne; i++)
			if ((128+i*LineHeight) > 325)
				nStartLine++;

		// Affiche tout le texte dans l'�cran off
		for (YPos=(128+LineHeight*(nLigne-nStartLine)), i=0; i<=nLigne-nStartLine; YPos-=LineHeight, i++)
		{
			// Affiche sur l'�cran off
			if (i == 0)
				offScrGC.drawString(m_strLigne[nLigne-i].substring(0,nChar) + strCurseur, 147, YPos);
			else
				offScrGC.drawString(m_strLigne[nLigne-i], 147, YPos);
		}

		// Affiche � l'�cran
		g.drawImage(offScrImage, 0, 0, this);
		bBusy = false;
	}

	//		La m�thode start() est appel�e lorsque la page contenant l'applet
	// s'affiche en premier � l'�cran. L'impl�mentation initiale de l'Assistant Applet
	// pour cette m�thode d�marre l'ex�cution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void start()
	{
		if (m_Terminal == null)
		{
			m_Terminal = new Thread(this);
			m_Terminal.start();
		}
		// TODO: Ins�rez ici des lignes de code suppl�mentaires pour le d�marrage de l'applet
	}
	
	//		La m�thode stop() est appel�e lorsque la page contenant l'applet
	// dispara�t de l'�cran. L'impl�mentation initiale de l'Assistant Applet
	// pour cette m�thode arr�te l'ex�cution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void stop()
	{
		if (m_Terminal != null)
		{
			m_Terminal.stop();
			m_Terminal = null;
		}

		// TODO: Ins�rez ici des lignes de code suppl�mentaires destin�es � arr�ter l'applet
	}

	// PRISE EN CHARGE THREAD
	//		La m�thode run() est appel�e lorsque la thread de l'applet est d�marr�e.
	// Si votre applet effectue des activit�s permanentes sans attendre la saisie de donn�es par l'utilisateur
	// le code impl�mentant ce comportement s'ins�re en r�gle g�n�rale ici. Par
	// exemple,  pour une applet qui r�alise une animation,  la m�thode run() g�re
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
				else // Caract�re suivant
				{
					Thread.sleep( (int) (100*Math.random()) );
					nChar++;
				}

				// Son des touches
//				if (acBeep != null)
//					acBeep.play();

				// Demande � afficher
				repaint();
			}
			catch (InterruptedException e)
			{
				// TODO: Ins�rez ici les lignes de code destin�es � traiter les exceptions au cas o�
				//       InterruptedException lev�e par Thread.sleep(),
				//		 ce qui signifie qu'une autre thread a interrompu celle-ci
				stop();
			}
		}
	}



	// TODO: Ins�rez ici des lignes de code suppl�mentaires pour l'applet

}
