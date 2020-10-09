package game;

import java.io.DataOutputStream;
import java.io.IOException;

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
	
	private DataOutputStream output;

	public SignInDialog() {
	    this.initStyle(StageStyle.UTILITY);
	    this.initModality(Modality.APPLICATION_MODAL);
	    this.setResizable(false);
	    this.setTitle("Sign In");
	    
	    
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
	
	
	public String confirm() {
		String userName = "";
		if (txtUsername.getText() != null) {
			userName = txtUsername.getText().trim();
			this.close();
			return userName;
		}
		return userName;
	}
	
	
	public void closeWindow() {
		this.close();
	}
}
