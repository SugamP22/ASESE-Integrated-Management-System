package ui.views.app;

import ui.controllers.root.RootController;
import ui.views.content.ContentLayerView;
import ui.views.root.RootView;

import javax.swing.*;
import java.awt.*;

public class AppView extends JFrame {

	private RootView rootView;

	public AppView() {
		properties();
		createRootView();
		wireControllers();
	}

	private void properties() {
		this.setTitle("PepeLink");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setMinimumSize(new java.awt.Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
	}

	private void createRootView() {
		rootView = new RootView();
		setContentPane(rootView);
	}

	private void wireControllers() {
		ContentLayerView contentLayerView = rootView.getContentLayerView();
		new RootController(contentLayerView);
	}
}
