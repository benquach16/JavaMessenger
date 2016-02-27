//factory method pattern
//create panels from this class

package JMessage;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TerminalPosition; 

public class PanelFactory
{
	public PanelFactory()
	{
		
	}

	public Window createMainWindow()
	{
		BasicWindow mainWindow = new BasicWindow();
		return mainWindow;
	}

	public Window createMessagePopup(MultiWindowTextGUI gui, String str)
	{
		BasicWindow window = new BasicWindow();
		Panel panel = new Panel();
		panel.addComponent(new Label(str));

		
		window.setComponent(panel);
		gui.addWindowAndWait(window);
		
		return window;		
	}

	//take in a handle
	public Window createRegisterWindow(MultiWindowTextGUI gui, Messenger esql)
	{
		BasicWindow registerWindow = new BasicWindow();
		Panel registerPanel = new Panel();

		TextBox usernameText = new TextBox();
		TextBox passwordText = new TextBox();
		TextBox phoneText = new TextBox();
		Button submitButton = new Button("Submit",
			 new Runnable()
			 {
				 public void run()
				 {
					 //grabh information from text files and insert them into a database
					 String login = usernameText.getText();
					 String password = passwordText.getText();
					 String phone = phoneText.getText();
					 try
					 {
						 
						 //Creating empty contact\block lists for a user
						 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
						 int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
						 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
						 int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
						 String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

						 esql.executeUpdate(query);

						 
						 //create a textbox displaying success or error
						 BasicWindow successWindow = new BasicWindow();
						 Panel successPanel = new Panel();
						 successPanel.addComponent(new Label("Successfully registered user!"));
						 successPanel.addComponent(new Button("OK",
								  new Runnable()
								  {
									  public void run()
									  {
										  successWindow.close();
										  registerWindow.close();
									  }
								  }));
						 successWindow.setComponent(successPanel);
						 gui.addWindowAndWait(successWindow);
						 
					 }
					 catch(Exception e)
					 {
						 //find out how to print real debug msgs
						 String str = e.getMessage();
						 //createMessagePopup(gui, e.getMessage());
						 createMessagePopup(gui, "Failed to create user");
					 }
						
				 }
			 });

		
		Button cancelButton = new Button("Cancel",
			 new Runnable()
			 {
				 public void run()
				 {
					 registerWindow.close();
				 }
			 });
		registerPanel.setLayoutManager(new GridLayout(2));
		registerPanel.addComponent(new Label("Username:"));
		registerPanel.addComponent(usernameText);
		registerPanel.addComponent(new Label("Password:"));
		registerPanel.addComponent(passwordText);
		registerPanel.addComponent(new Label("Phone:"));
		registerPanel.addComponent(phoneText);
		registerPanel.addComponent(new EmptySpace(new TerminalSize(1,1)));
		registerPanel.addComponent(new EmptySpace(new TerminalSize(1,1)));
		registerPanel.addComponent(submitButton);
		registerPanel.addComponent(cancelButton);

		//create user here
		registerWindow.setComponent(registerPanel);
		gui.addWindowAndWait(registerWindow);
		return registerWindow;
	}

	//all the messaging stuff should be here
	//chat lists, etc
	public Window createUserWindow(MultiWindowTextGUI gui)
	{
		BasicWindow userWindow = new BasicWindow();
		userWindow.setHints(Arrays.asList(Window.Hint.EXPANDED));
		Panel userPanel = new Panel();
		userPanel.setLayoutManager(new GridLayout(4));

		userWindow.setComponent(userPanel);
		gui.addWindowAndWait(userWindow);
		return userWindow;
	}

}//end PanelFactory
