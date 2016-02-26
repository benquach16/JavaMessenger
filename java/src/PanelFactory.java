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

	public Window createRegisterWindow()
	{
		BasicWindow registerWindow = new BasicWindow();
		Panel registerPanel = new Panel();

		TextBox usernameText = new TextBox();
		TextBox passwordText = new TextBox();
		Button submitButton = new Button("Submit",
										 new Runnable()
										 {
											 public void run()
											 {
												 //grabh information from text files and insert them into a database
												 String username = usernameText.getText();
												 String password = passwordText.getText();
					
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
		registerPanel.addComponent(new TextBox());
		registerPanel.addComponent(new EmptySpace(new TerminalSize(1,1)));
		registerPanel.addComponent(new EmptySpace(new TerminalSize(1,1)));
		registerPanel.addComponent(submitButton);
		registerPanel.addComponent(cancelButton);

		//create user here
		registerWindow.setComponent(registerPanel);
		return registerWindow;
	}

	//all the messaging stuff should be here
	//chat lists, etc
	public Window createUserWindow()
	{
		BasicWindow userWindow = new BasicWindow();

		
		return userWindow;
	}

}//end PanelFactory
