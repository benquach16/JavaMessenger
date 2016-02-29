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
		final BasicWindow window = new BasicWindow();
		Panel panel = new Panel();
		panel.addComponent(new Label(str));
		panel.addComponent(new Button("OK",
					      new Runnable()
					      {
						  //close button
						  public void run()
						      {
							  window.close();
						      }
					      }));
		
		window.setComponent(panel);
		gui.addWindowAndWait(window);
		
		return window;		
	}

	public Window createChatWindow(final MultiWindowTextGUI gui, final Messenger esql)
	{
	    final BasicWindow window = new BasicWindow();
	    return window;
	}

	//take in a handle
	public Window createRegisterWindow(final MultiWindowTextGUI gui, final Messenger esql)
	{
		final BasicWindow registerWindow = new BasicWindow();
		Panel registerPanel = new Panel();

		final TextBox usernameText = new TextBox();
		final TextBox passwordText = new TextBox();
		final TextBox phoneText = new TextBox();
		Button submitButton = new Button
		    ("Submit",
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
						 
				     esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
				     int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
				     esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
				     int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
				     String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

				     esql.executeUpdate(query);						 
				     //create a textbox displaying success or error
				     final BasicWindow successWindow = new BasicWindow();
				     final Panel successPanel = new Panel();
				     successPanel.addComponent(new Label("Successfully registered user!"));
				     successPanel.addComponent(new Button
							       ("OK",
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
				     Thread t = Thread.currentThread();
				     t.getUncaughtExceptionHandler().uncaughtException(t, e);
				     createMessagePopup(gui, e.getMessage());
				     //createMessagePopup(gui, "Failed to create user");
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
    public Window createUserWindow(final MultiWindowTextGUI gui, final Messenger esql) {
	final BasicWindow userWindow = new BasicWindow();
	//userWindow.setHints(Arrays.asList(Window.Hint.EXPANDED));
	userWindow.setHints(Arrays.asList(Window.Hint.CENTERED));
	Panel userPanel = new Panel();
	userPanel.setLayoutManager(new LinearLayout());
	TerminalSize size = new TerminalSize(20, 10);
	ActionListBox actionListBox = new ActionListBox(size);
	//DO A SQL QUERY HERE SO THAT WE CAN GET LIST OF CHATS
	try
	{
	    String query = String.format("SELECT chat_id FROM CHAT_LIST WEHRE member = '%s'", Messenger._currentUser);
	    List<List<String>> ret = esql.executeQueryAndReturnResult(query);
	    for(int i = 0; i < ret.size(); i++)
	    {
		actionListBox.addItem("Test", new Runnable()
		    {
			public void run()
			    {
			    }
		    });
	    }
	}
	catch(Exception e)
	{
	}
		

	Panel chatPanel = new Panel();
	chatPanel.addComponent(actionListBox);
	userPanel.addComponent(chatPanel.withBorder(Borders.singleLine("List of chats you're in")));
	userPanel.addComponent(new Button("Add a user to a Contacts List"));
	userPanel.addComponent(new Button("Manage Contacts"));
	userPanel.addComponent(new Button(
				   "Create Chat", new Runnable()
				       {
					   public void run()
					       {
						   createCreateChatWindow(gui, esql);
					       }
				       }));

	userPanel.addComponent(new Button(
				   "Logout", new Runnable()
				       {
					   public void run()
					       {
						   userWindow.close();
					       }
				       }));
		
	userWindow.setComponent(userPanel.withBorder(Borders.doubleLine(Messenger._currentUser)));
	gui.addWindowAndWait(userWindow);
	return userWindow;
    }



    //haha
    public Window createCreateChatWindow(MultiWindowTextGUI gui, Messenger esql)
	{
		final BasicWindow chatWindow = new BasicWindow();	    
		//use this to create a chat !
		//let users add people to chat channel
		Panel chatPanel = new Panel();
		
		Button createButton = new Button
		    ("Create", new Runnable()
			{
			    public void run()
				{
				    //create with the initialized users list
				    
				    
				}
			});
		TextBox friends = new TextBox();
		chatPanel.addComponent(friends.withBorder(Borders.singleLine("Add Friends")));
		chatPanel.addComponent(createButton);
		chatPanel.addComponent(new Button(
					   "Cancel",
					   new Runnable()
					   {
					       public void run()
						   {
						       chatWindow.close();
						   }
					   }));
		chatWindow.setComponent(chatPanel.withBorder(Borders.doubleLine("Create a chat")));
		gui.addWindowAndWait(chatWindow);
		return chatWindow;
	}

    public Window createAddUsersWindow(MultiWindowTextGUI gui, Messenger esql)
	{
	    BasicWindow userWindow = new BasicWindow();	    
	    Panel userPanel = new Panel();
	    userWindow.setComponent(userPanel);
	    return userWindow;
	}

}//end PanelFactory
