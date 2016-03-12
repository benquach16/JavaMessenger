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
import java.util.concurrent.atomic.AtomicInteger;

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
    //god, please forgive me for the code i am about to write
    // For certainly Tony does not
    
	public static String _currentChatId;
    public static String _currentMessageId;
	
	public PanelFactory()
	{
		
	}

	public Window createMainWindow()
	{
		BasicWindow mainWindow = new BasicWindow();
		return mainWindow;
	}


    public void regenerateChatMessages(Panel panel,ActionListBox usersInChat, final MultiWindowTextGUI gui,final Messenger esql, AtomicInteger msgNum)
	{
	    //delete all components from a panel then requery
	    panel.removeAllComponents();
	    usersInChat.clearItems();
	    try
	    {
		//fetch only latest 10
		String query = String.format("SELECT * FROM MESSAGE WHERE chat_id='%s' ORDER BY msg_timestamp DESC LIMIT %d;", _currentChatId, msgNum.intValue());
		List<List<String>> ret = esql.executeQueryAndReturnResult(query);
		for(int i = ret.size()-10; i < ret.size(); i++)
		{
			final int uhh = i;
			_currentMessageId = ret.get(uhh).get(0);
			Panel nPanel = new Panel();
		    nPanel.setLayoutManager(new GridLayout(2));
		    nPanel.addComponent(new Label(ret.get(i).get(1).trim() + " on " + ret.get(i).get(2).trim()));
		    nPanel.addComponent(new EmptySpace(new TerminalSize(0,0)));
		    nPanel.addComponent(
			new Button("Edit",
				   new Runnable()
				   {
				       public void run()
					   {
					       //do a query so we dont let the wrong user edit the message
					       try
					       {
					       		createEditMessageWindow(gui,esql);
					       }
					       catch(Exception e)
					       {
					       }

					   }
				   }));
		    nPanel.addComponent(
			new Button("Delete",
				   new Runnable()
				   {
				       public void run()
					   {
					       //just delete here
					       try
					       {
					       	// Need to get _msgID...maybe that worked
					       	String query4 = String.format("DELETE FROM MESSAGE M USING USR U WHERE M.msg_id='%s' AND U.login='%s' AND U.login=M.sender_login;", _currentMessageId, Messenger._currentUser);
					       	esql.executeQuery(query4);
					       }
					       catch(Exception e)
					       {
					       }
					   }
				   }));
		    panel.addComponent(nPanel.withBorder(Borders.singleLine(ret.get(i).get(3).trim())));
		}

		//now fetch users in the chat
		String query2 = String.format("SELECT member FROM CHAT_LIST WHERE chat_id='%s';", _currentChatId);
		List<List<String>> ret2 = esql.executeQueryAndReturnResult(query2);
		for(int i = 0; i < ret2.size(); i++)
		{
		    usersInChat.addItem(
			ret2.get(i).get(0).trim(),
			new Runnable()
			{
			    public void run()
				{
				}
			}
			);
		}
	    }
	    catch(Exception e)
	    {
		
	    }
	}


    
    public Window createEditMessageWindow(final MultiWindowTextGUI gui, final Messenger esql)
	{

	    final BasicWindow window = new BasicWindow();
	    window.setHints(Arrays.asList(Window.Hint.CENTERED));
	    final Panel panel = new Panel();
	    final TextBox text = new TextBox();
	    panel.addComponent(text);
	    panel.addComponent(
		new Button("Edit",
				new Runnable()
				{
					public void run()
					{
						String updateString = text.getText().trim();
						try{
							// Need to get _msgID and the text...maybe that worked
					    	String query = String.format("UPDATE MESSAGE SET msg_text='%s', msg_timestamp=now() FROM USR WHERE USR.login='%s' AND USR.login=MESSAGE.sender_login AND MESSAGE.msg_id='%s'; ",updateString, Messenger._currentUser, _currentMessageId);
					    	esql.executeQuery(query);
						}
						catch(Exception e){
							//createMessagePopup(gui, "Cannot edit: You are not message author!");
						}
					}
				}));
	    panel.addComponent(
		new Button("Cancel",
			   new Runnable()
			   {
			       public void run()
				   {
				       window.close();
				   }
			   }));
	    window.setComponent(panel.withBorder(Borders.doubleLine("Edit Message")));
	    gui.addWindowAndWait(window);
	    return window;
	}

    public Window createSetStatusWindow(MultiWindowTextGUI gui, final Messenger esql)
	{
	    final BasicWindow window = new BasicWindow();
	    window.setHints(Arrays.asList(Window.Hint.CENTERED));
	    final Panel panel = new Panel();
	    final TextBox text = new TextBox();
	    panel.addComponent(text);
	    panel.addComponent(
		new Button("Edit",
				new Runnable()
				{
					public void run()
					{
						String updateString = text.getText().trim();
						try{
							// Need to get _msgID and the text...maybe that worked
					    	String query = String.format("UPDATE USR SET status='%s' WHERE USR.login='%s';",updateString, Messenger._currentUser);
					    	esql.executeQuery(query);
						}
						catch(Exception e){
							
						}
					}
				}));
	    panel.addComponent(
		new Button("Cancel",
			   new Runnable()
			   {
			       public void run()
				   {
				       window.close();
				   }
			   }));
	    window.setComponent(panel.withBorder(Borders.doubleLine("Edit Status")));
	    gui.addWindowAndWait(window);	    
	    return window;
	}

	public Window createMessagePopup(MultiWindowTextGUI gui, String str)
	{
		final BasicWindow window = new BasicWindow();
		Panel panel = new Panel();
		panel.addComponent(new Label(str));
		panel.addComponent(
		    new Button("OK",
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
	    window.setHints(Arrays.asList(Window.Hint.CENTERED));
	    Panel mainPanel = new Panel();
	    mainPanel.setLayoutManager(new GridLayout(2));
	    Panel usersPanel = new Panel();
	    Panel chatPanel = new Panel();
	    mainPanel.addComponent(usersPanel);

	    final Panel panel = new Panel();

	    final TextBox inputString = new TextBox();
	    final ActionListBox usersInChat = new ActionListBox();
	    
	    usersPanel.addComponent(usersInChat.withBorder(Borders.singleLine("Users")));
	    final TextBox friends = new TextBox();
	    final AtomicInteger msgNum = new AtomicInteger(10);
	    usersPanel.addComponent(friends);
	    usersPanel.addComponent(
		new Button("Add Users",
			   new Runnable()
			   {
			       public void run()
				   {
				       //parse friends to add
				       try
				       {
				       String query3;
				       String[] parseFriends = friends.getText().split(",");
				       for (int i = 0; i < parseFriends.length; ++i) {
					   query3 = String.format("INSERT INTO CHAT_LIST VALUES ('%s', '%s');", _currentChatId, parseFriends[i]);
					   esql.executeUpdate(query3);
				       }
				       }
				       catch(Exception e)
				       {
					   
				       }
				   }
			   }));
	    
	    usersPanel.addComponent(new Button("Delete Chat",
	    		new Runnable()
	    		{
	    			public void run()
	    			{
	    				try {
		    				String isDeleteable = String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id='%s';", Messenger._currentUser, _currentChatId);
		    				List<List<String>> ret = esql.executeQueryAndReturnResult(isDeleteable);
		    				final String tmp = _currentChatId;
		    				if ( ret.get(0).get(1).length() > 0 ) {
			    				try {
			    					createMessagePopup(gui, "Goodbye friends :(");
			    					// Need to delete in order of messages --> chat list --> chat
			    					String queryDelMess = String.format("DELETE FROM MESSAGE WHERE chat_id='%s';", _currentChatId);
			    					esql.executeUpdate(queryDelMess);
			    				}
			    				catch(Exception e) {
			    					// For some reason, it goes into if, does the first query and doesn't do the rest, but they all individually work
			    					//createMessagePopup(gui, "Cannot delete: You are not the chat creator!");		
			    				}
			    				try {
			    					String queryDelChtL = String.format("DELETE FROM CHAT_LIST WHERE chat_id='%s';", _currentChatId);
			    					esql.executeUpdate(queryDelChtL);
			    				}
			    				catch (Exception e) {
			    					
			    				}
			    				try {
			    					String queryDelChat = String.format("DELETE FROM CHAT WHERE chat_id='%s' AND init_sender='%s';",_currentChatId, Messenger._currentUser);
			    					esql.executeUpdate(queryDelChat);
			    				}
			    				catch (Exception e) {
			    					
			    				}
		    				}
	    				}
	    				catch (Exception e) {
	    					
	    				}
	    			}
	    		}
	    ));
	    regenerateChatMessages(panel, usersInChat, gui, esql, msgNum);

	    chatPanel.addComponent(
		new Button("Next",
			   new Runnable()
			   {
			       public void run()
				   {
				       msgNum.getAndSet(msgNum.intValue() + 10);
				       regenerateChatMessages(panel,usersInChat,gui,esql,msgNum);
				   }
			   }));

	    chatPanel.addComponent(inputString);
	    Button enter = new Button("Send!",
				      new Runnable()
				      {
					  public void run()
					      {
						  //do stuff here
						  try{
						      String textToSend = inputString.getText();
						      String query = String.format("INSERT INTO MESSAGE VALUES (DEFAULT, '%s', now(), '%s', '%s');", textToSend, Messenger._currentUser, _currentChatId);
						      // catch for msg length
						      esql.executeUpdate(query);
						      msgNum.getAndSet(10);
						      regenerateChatMessages(panel,usersInChat,gui,esql,msgNum);
						  }
						  catch(Exception e) {
							// These are more trouble than they're worth
							String str = e.getMessage();
							Thread t = Thread.currentThread();
							t.getUncaughtExceptionHandler().uncaughtException(t, e);
							createMessagePopup(gui, e.getMessage());	
							//Pretty sure this also doesn't work as intended
						      /*try
						      {
							  	gui.getScreen().refresh(Screen.RefreshType.COMPLETE);
						      }
						      catch(Exception ex)
						      {
							  	createMessagePopup(gui, "could not refresh");
						      }*/
						  }
					      }
				      });

	    chatPanel.addComponent(enter);
	    chatPanel.addComponent(new Button("Quit",
		       new Runnable()
		       {
			   public void run()
			      {
				  window.close();
			      }
		       }));
	    mainPanel.addComponent(panel);
	    mainPanel.addComponent(new EmptySpace(new TerminalSize(0,0)));
	    mainPanel.addComponent(chatPanel);
	    window.setComponent(mainPanel.withBorder(Borders.doubleLine(_currentChatId)));

	    gui.addWindowAndWait(window);
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
				     successPanel.addComponent(
					 new Button
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
				     try
				     {
					 gui.getScreen().refresh(Screen.RefreshType.COMPLETE);
				     }
				     catch(Exception ex)
				     {
					 createMessagePopup(gui, "could not refresh");
				     }
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
	TerminalSize size = new TerminalSize(30, 10);
	ActionListBox actionListBox = new ActionListBox(size);
	//DO A SQL QUERY HERE SO THAT WE CAN GET LIST OF CHATS
	try
	{
	    //String query = String.format("SELECT * FROM CHAT_LIST WHERE member='%s';", Messenger._currentUser);
	    String query = String.format("SELECT C.* FROM CHAT C, CHAT_LIST CL WHERE C.chat_id = CL.chat_id AND CL.member='%s';", Messenger._currentUser);
	    final List<List<String>> ret = esql.executeQueryAndReturnResult(query);
	    for(int i = 0; i < ret.size(); i++)
	    {
	    	final int k = i;
		actionListBox.addItem("Chat " + ret.get(i).get(0) + " Init Sender: " + ret.get(i).get(2), new Runnable()
		    {
			public void run()
			    {
				//open a chat window
				final int jesus = k;
				_currentChatId = ret.get(jesus).get(0);
				createChatWindow(gui, esql);
			    }
		    });
	    }
	}
	catch(Exception e)
	{
	    Thread t = Thread.currentThread();
	    t.getUncaughtExceptionHandler().uncaughtException(t, e);
	    createMessagePopup(gui, e.getMessage());
	}
	;		

	Panel chatPanel = new Panel();
	chatPanel.addComponent(actionListBox);
	userPanel.addComponent(chatPanel.withBorder(Borders.singleLine("List of chats you're in")));
	userPanel.addComponent(
	    new Button(
		"Add a user to a Contacts List",
		new Runnable()
		{
		    public void run()
			{
			    createAddUsersWindow(gui, esql);
			}
		}));
	userPanel.addComponent(
	    new Button(
		"Manage Contacts",
		new Runnable()
		{
		    public void run()
			{
			    createShowContactsWindow(gui,esql);
			}
		}));
	userPanel.addComponent(
	    new Button(
		"Create Chat", 
		new Runnable()
		{
		    public void run()
			{
			    createCreateChatWindow(gui, esql);
			}
		}));
	userPanel.addComponent(
	    new Button(
		"Set User Status",
		new Runnable()
		{
		    public void run()
			{
				createSetStatusWindow(gui, esql);
			}
		}));
	userPanel.addComponent(
	    new Button(
		"Delete User",
		new Runnable()
		{
		    public void run()
			{
				try{
					// Since Chat and Chat_List reference USR, should be unable to delete unless not in a chat
					String query = String.format("DELETE FROM USR WHERE login='%s';", Messenger._currentUser);
					esql.executeQuery(query);
				}
				catch( Exception e ) {
					// Seems to pop up no matter what
					createMessagePopup(gui, "Could not delete user, user it still in a chat!");
				}
			}
		}));

	userPanel.addComponent(
	    new Button(
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


    public Window createShowContactsWindow(final MultiWindowTextGUI gui, final Messenger esql)
	{
	    final BasicWindow contactsWindow = new BasicWindow();
	    Panel contactsPanel = new Panel();
	    //show both friends and blocked contacts here
	    ActionListBox friendListBox = new ActionListBox();
	    ActionListBox blockListBox = new ActionListBox();
	    contactsPanel.setLayoutManager(new GridLayout(2));
	    //two queries here - find friends in blocked list and friends list
	    try
	    {
		String query = String.format("SELECT ULC.list_member FROM USR U, USER_LIST_CONTAINS ULC WHERE U.login = '%s' AND U.block_list = ULC.list_id;", Messenger._currentUser);
		final List<List<String>> ret = esql.executeQueryAndReturnResult(query);
		for(int i = 0; i < ret.size(); i++)
		{
			final int k = i;
			blockListBox.addItem(ret.get(i).get(0).trim(), new Runnable()
		    {
			public void run()
			    {   //createMessagePopup(gui, ret.get(k).get(0));
				//add a button here
				
				
			    	try{
   
				    	String query420 = String.format("DELETE FROM USER_LIST_CONTAINS ULC USING USR U WHERE ULC.list_member='%s' AND U.block_list=ULC.list_id AND U.login='%s';", ret.get(k).get(0), Messenger._currentUser);
				    	esql.executeQuery(query420);
			    	}
			    	catch(Exception e) {

			    	}
			    }
		    });
		}
		String query2 = String.format("SELECT login, status FROM usr WHERE login IN (SELECT ULC.list_member FROM USR U, USER_LIST_CONTAINS ULC WHERE U.login = '%s' AND U.contact_list = ULC.list_id);", Messenger._currentUser);
		final List<List<String>> ret2 = esql.executeQueryAndReturnResult(query2);
		for(int i = 0; i < ret2.size(); i++)
		{
			final int j = i;
			friendListBox.addItem(ret2.get(i).get(0).trim() + ": " + ret2.get(i).get(1).trim(), new Runnable()
		    {
			public void run()
			    {
			    	try{ 
				    	String query420BlazeIt = String.format("DELETE FROM USER_LIST_CONTAINS ULC USING USR U WHERE ULC.list_member='%s' AND U.contact_list=ULC.list_id AND U.login='%s';", ret2.get(j).get(0), Messenger._currentUser);
				    	esql.executeQuery(query420BlazeIt);
			    	}
			    	catch(Exception e) {
				     String str = e.getMessage();
				     Thread t = Thread.currentThread();
				     t.getUncaughtExceptionHandler().uncaughtException(t, e);
				     createMessagePopup(gui, e.getMessage());
				     try
				     {
					 gui.getScreen().refresh(Screen.RefreshType.COMPLETE);
				     }
				     catch(Exception ex)
				     {
					 createMessagePopup(gui, "could not refresh");
				     }
			    	}
			    }
		    });
		}
	    }
	    catch (Exception e)
	    {
		String str = e.getMessage();
		Thread t = Thread.currentThread();
		t.getUncaughtExceptionHandler().uncaughtException(t, e);
		createMessagePopup(gui, e.getMessage());					  
		try
		{
		    gui.getScreen().refresh(Screen.RefreshType.COMPLETE);
		}
		catch(Exception ex)
		{
		    createMessagePopup(gui, "could not refresh");
		}		
	    }
	    contactsPanel.addComponent(friendListBox.withBorder(Borders.singleLine("Friends List")));
	    contactsPanel.addComponent(blockListBox.withBorder(Borders.singleLine("Blocked List")));
	    contactsPanel.addComponent(
		new Button("Cancel",
			   new Runnable()
			   {
			       public void run()
				   {
				       contactsWindow.close();
				   }
			   }));
	    contactsWindow.setComponent(contactsPanel.withBorder(Borders.doubleLine("Contacts")));
	    gui.addWindowAndWait(contactsWindow);
	    return contactsWindow;
	}

    //haha
    public Window createCreateChatWindow(final MultiWindowTextGUI gui, final Messenger esql)
	{
		final BasicWindow chatWindow = new BasicWindow();	    
		//use this to create a chat !
		//let users add people to chat channel
		Panel chatPanel = new Panel();
		final TextBox friends = new TextBox();
		Button createButton = new Button
		    ("Create", new Runnable()
			{
			    public void run()
				{
				    //create with the initialized users list
				    //this requires two queries - one to create the chat and the other to add all users to the chat users table
				    try
				    {
						//this is just a test!!!
						String query1 = String.format("INSERT INTO CHAT VALUES (DEFAULT, 'PRIVATE', '%s');", Messenger._currentUser);
						String query2 = String.format("INSERT INTO CHAT_LIST VALUES ((SELECT chat_id from CHAT ORDER BY chat_id DESC LIMIT 1), '%s');", Messenger._currentUser);				    
						esql.executeUpdate(query1);						 
						esql.executeUpdate(query2);
						
						String query3;
						String[] parseFriends = friends.getText().split(",");
						for (int i = 0; i < parseFriends.length; ++i) {
							query3 = String.format("INSERT INTO CHAT_LIST VALUES ((SELECT chat_id from CHAT ORDER BY chat_id DESC LIMIT 1), '%s');", parseFriends[i]);
							esql.executeUpdate(query3);
						}
				    }
				    catch(Exception e)
				    {

				     Thread t = Thread.currentThread();
				     t.getUncaughtExceptionHandler().uncaughtException(t, e);
				     createMessagePopup(gui, e.getMessage());
				     try
				     {
					 gui.getScreen().refresh(Screen.RefreshType.COMPLETE);
				     }
				     catch(Exception ex)
				     {
					 createMessagePopup(gui, "could not refresh");
				     }
				    }
				}
			});
		chatPanel.addComponent(friends.withBorder(Borders.singleLine("Add Friends")));
		chatPanel.addComponent(createButton);
		chatPanel.addComponent(
		    new Button(
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

    public Window createAddUsersWindow(final MultiWindowTextGUI gui, final Messenger esql)
	{
	    final BasicWindow userWindow = new BasicWindow();	    
	    Panel userPanel = new Panel();

	    final ComboBox<String> comboBox = new ComboBox<String>();
	    final TextBox friendName = new TextBox();

	    userPanel.addComponent(friendName);
	    comboBox.addItem("Friends");
	    comboBox.addItem("Blocked");
	    userPanel.addComponent(comboBox);
	    userPanel.addComponent(
		new Button("Add User",
			   new Runnable()
			   {
			       public void run()
				   {
				       //do a sql query here to find the friend
				       //then we can add them to the table contacts_list
				       try
				       {
					   
					   String usern = friendName.getText();
				       
					   //String selectQuery = String.format("SELECT ULC.list_member FROM USR U, USER_LIST UL, USER_LIST_CONTAINS ULC WHERE U.login = '%s' AND UL.list_id = ULC.list_id AND U.block_list = UL.list_id AND ULC.list_member = '%s';", Messenger._currentUser, usern);
					   //List<List<String>> ret = esql.executeQueryAndReturnResult(selectQuery);
						   if (comboBox.getSelectedIndex() == 0 ) {
							       //no return so we can insert since he is not ni the block list
							       String insertQuery = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES ( (SELECT contact_list FROM USR WHERE login='%s' ), '%s');", Messenger._currentUser, usern);	
							       int rows = esql.executeQuery(insertQuery);
							       //should work now
						   }
						   else {
						   		   //no return so we can insert since he is not ni the block list
							       String insertQuery = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES ( (SELECT block_list FROM USR WHERE login='%s' ), '%s');", Messenger._currentUser, usern);	
							       int rows = esql.executeQuery(insertQuery);
							       //should work now
						   }
				       }
				       catch(Exception e)
				       {
					   String str = e.getMessage();
					   Thread t = Thread.currentThread();
					   t.getUncaughtExceptionHandler().uncaughtException(t, e);
					   createMessagePopup(gui, e.getMessage());					  
					   try
					   {
					       gui.getScreen().refresh(Screen.RefreshType.COMPLETE);
					   }
					   catch(Exception ex)
					   {
					       createMessagePopup(gui, "could not refresh");
					   }
				       }
				   }
			   }));
	    userPanel.addComponent(
		new Button("Cancel",
			   new Runnable()
			   {
			       public void run()
				   {
				       userWindow.close();
				   }
			   }));

	    userWindow.setComponent(userPanel.withBorder(Borders.doubleLine("Add A Contact")));
	    
	    gui.addWindowAndWait(userWindow);
	    return userWindow;
	}

}//end PanelFactory
