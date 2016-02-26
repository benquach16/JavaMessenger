//factory method pattern
//create panels from this class

package JMessage;

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
		registerPanel.setLayoutManager(new GridLayout(2));
		registerPanel.addComponent(new Label("Username"));
		registerPanel.addComponent(new TextBox());
		registerPanel.addComponent(new Label("Password"));
		registerPanel.addComponent(new TextBox());
		registerPanel.addComponent(new EmptySpace(new TerminalSize(1,1)));
		registerPanel.addComponent(new EmptySpace(new TerminalSize(1,1)));
		registerPanel.addComponent(new Button("Submit"));
		registerPanel.addComponent(new Button("Cancel"));

		
		registerWindow.setComponent(registerPanel);
		return registerWindow;
	}
}//end PanelFactory
