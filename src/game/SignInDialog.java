package game;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SignInDialog extends Stage {
	
	private Label lblUsername;
	private TextField txtUsername;
	private Button btnConfirm;
	
	private Player me;

	public SignInDialog(Player player) {
	    this.initStyle(StageStyle.UTILITY);
	    this.initModality(Modality.APPLICATION_MODAL);
	    this.setResizable(false);
	    this.setTitle("Sign In");
	    
	    this.me = player;
	    
        GridPane pane = new GridPane();
        Scene scene = new Scene(pane);
       
        this.initContent(pane);
        this.setScene(scene);
	}
	
	
	private void initContent(GridPane pane) {
		pane.setPadding(new Insets(20));
	    pane.setHgap(20);
	    pane.setVgap(10);
	    pane.setGridLinesVisible(false);
	    
	    
	    lblUsername = new Label("Enter a username: ");
	    pane.add(lblUsername, 0, 0);
	    
	    txtUsername = new TextField();
	    pane.add(txtUsername, 1, 0);
	    
	    btnConfirm = new Button("enter game");
	    btnConfirm.setOnAction(event -> this.confirm());
	    pane.add(btnConfirm, 2, 0);
	    
	}
	
	
	public void confirm() {
		if (txtUsername.getText() != null) {
			String username = txtUsername.getText().trim();
			System.out.println(username);
			this.me.setName(username);
			
			this.closeWindow();
		}
	}
	
	
	public void closeWindow() {
		this.close();
	}
}
