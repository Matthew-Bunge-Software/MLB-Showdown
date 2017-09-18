//Remember to edit draft lists to show player data

package graphicsInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import gameData.*;
import players.PlayerComparator;
import players.PlayerData;

public class MainRunner {
	
	private static LineupManager teamOne;
	private static LineupManager teamTwo;
	
	public static void main(String[] args) throws FileNotFoundException {
		DraftManager mainPool = DraftManager.initializePool(new File("2004 pitchers.txt"), new File("2004 hitters.txt"));
		Map<String, PlayerData> pool = mainPool.getPool();
		JFrame mainWindow = createMainFrame();
		Font standardF = new Font(Font.SANS_SERIF, Font.PLAIN, (mainWindow.getWidth() + mainWindow.getHeight()) / 200); // Arbitrary
		JFrame draftWindow = createDraftFrame(mainPool, standardF);
		JFrame poolWindow = createListFrame(pool, standardF);
		JFrame lineupWindow = createLineupFrame(pool, standardF);
		JMenuBar menuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("Main");
		JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent e) {
				draftWindow.setVisible(true);
			}
		});
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lineupWindow.setVisible(true);
			}
		});
		JMenuItem poolButton = new JMenuItem("View All Players");
		poolButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				poolWindow.setVisible(true);
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.dispose();
			}
		});
		newGame.setFont(standardF);
		mainMenu.add(newGame);
		load.setFont(standardF);
		mainMenu.add(load);
		poolButton.setFont(standardF);
		mainMenu.add(poolButton);
		exit.setFont(standardF);
		mainMenu.add(exit);
		mainMenu.setFont(standardF);
		menuBar.add(mainMenu);
		mainWindow.setJMenuBar(menuBar);
		mainWindow.setVisible(true);
	}

	/**
	 * Creates the overall bottom level container uses in the rest of the interface
	 * 
	 * @return The JFrame containing the overall base of the interface
	 */
	private static JFrame createMainFrame() {
		JFrame mainWindow = new JFrame("MLB Showdown");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.setSize(screenSize);
		return mainWindow;
	}

	private static JFrame createDraftFrame(DraftManager pool, Font f) {
		teamOne = new LineupManager();
		teamTwo = new LineupManager();
		JFrame draftWindow = new JFrame("Draft");
		JTextArea cardInfo = generateCardInfo(f);
		JXList poolList = generatePoolList(pool.getPool(), f);
		poolList.setComparator(new PlayerComparator());
		poolList.setAutoCreateRowSorter(true);
		poolList.setSortOrder(SortOrder.ASCENDING);
		JPanel draftPanel = createDraftPanel(pool.getPool(), f, cardInfo, poolList);
		JPanel homeTeam = createDraftTeamPanel(pool, teamOne, f, cardInfo, poolList);
		JPanel awayTeam = createDraftTeamPanel(pool, teamTwo, f, cardInfo, poolList);
		homeTeam.setFont(f);
		awayTeam.setFont(f);
		draftWindow.add(draftPanel, BorderLayout.CENTER);
		draftWindow.add(homeTeam, BorderLayout.WEST);
		draftWindow.add(awayTeam, BorderLayout.EAST);
		draftWindow.setSize(2000, 1000);
		return draftWindow;
	}

	private static JTextArea generateCardInfo(Font f) {
		JTextArea cardInfo = new JTextArea();
		cardInfo = new JTextArea();
		cardInfo.setFont(f);
		cardInfo.setEditable(false);
		return cardInfo;
	}

	private static JXList generatePoolList(Map<String, PlayerData> pool, Font f) {
		JXList poolList = new JXList();
		DefaultListModel<PlayerData> lm = new DefaultListModel<PlayerData>();
		for (String s : pool.keySet()) {
			lm.addElement(pool.get(s));
		}
		poolList.setModel(lm);
		poolList.setFont(f);
		return poolList;
	}

	private static JPanel createDraftPanel(Map<String, PlayerData> pool, Font f, JTextArea cardInfo, JXList poolList) {
		JPanel draftPanel = new JPanel();
		cardInfo.setFont(f);
		cardInfo.setEditable(false);
		poolList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				try {
					cardInfo.setText(((PlayerData) poolList.getSelectedValue()).getCard());
				} catch (Exception e) {
					
				}
			}	
		});
		JScrollPane poolScroller = new JScrollPane(poolList);
		poolScroller.setViewportView(poolList);
		draftPanel.add(poolScroller);
		draftPanel.add(cardInfo, BorderLayout.EAST);
		return draftPanel;
	}

	private static JPanel createDraftTeamPanel(DraftManager pool, LineupManager myTeam, Font f, JTextArea cardInfo, JXList poolList) {
		Map<String, PlayerData> cloned = pool.getPool();
		JPanel draftPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JXList myTeamStuff = new JXList();
		myTeamStuff.setComparator(new PlayerComparator());
		myTeamStuff.setAutoCreateRowSorter(true);
		myTeamStuff.setSortOrder(SortOrder.ASCENDING);
		myTeamStuff.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				try {
					cardInfo.setText(myTeam.getTeam().get(myTeamStuff.getSelectedValue()).getCard());
				} catch (Exception e) {
					
				}
			}	
		});
		JScrollPane poolScroller = new JScrollPane(myTeamStuff);
		poolScroller.setViewportView(myTeamStuff);
		JButton draftButton = new JButton("Draft Player");
		draftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pool.draftPlayer(myTeam, poolList.getSelectedValue().toString());
				DefaultListModel<PlayerData> model = (DefaultListModel<PlayerData>) poolList.getModel();
				model.remove(model.indexOf(poolList.getSelectedValue()));
				poolList.setModel(model);
				poolList.setAutoCreateRowSorter(true);
				poolList.setSortOrder(SortOrder.ASCENDING);
				myTeamStuff.setListData(myTeam.getTeam().keySet().toArray(new String[25]));
			}
		});
		JButton otherButton = new JButton("Export Team");
		otherButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, PlayerData> teamPool = new HashMap<String, PlayerData>();
				int len = myTeamStuff.getModel().getSize();
				for (int i = 0; i < len; i++) {
					String s = (String) myTeamStuff.getModel().getElementAt(i);
					if (s != null) {
						teamPool.put(s, cloned.get(s));
					}
				}
				LineupManager lm = new LineupManager(teamPool);
				lm.export("Text area entry");
			}
		});
		draftButton.setFont(f);
		otherButton.setFont(f);
	    buttonPanel.add(draftButton, BorderLayout.SOUTH);
	    buttonPanel.add(otherButton, BorderLayout.NORTH);
	    draftPanel.add(poolScroller, BorderLayout.NORTH);
		draftPanel.add(buttonPanel, BorderLayout.SOUTH);
		return draftPanel;
	}

	private static JFrame createListFrame(Map<String, PlayerData> pool, Font f) {
		JFrame poolWindow = new JFrame("Draft Pool");
		JTextArea cardInfo = generateCardInfo(f);
		JXList poolList = generatePoolList(pool, f);
		poolList.setComparator(new PlayerComparator());
		poolList.setAutoCreateRowSorter(true);
		poolList.setSortOrder(SortOrder.ASCENDING);
		JPanel draftPanel = createDraftPanel(pool, f, cardInfo, poolList);
		poolWindow.add(draftPanel);
		draftPanel.setVisible(true);
		poolWindow.setSize(1000, 1000); // Arbitrary
		return poolWindow;
	}

	private static JFrame createLineupFrame(Map<String, PlayerData> pool, Font f) {
		JFrame mainWindow = new JFrame("Lineup Editor");
		JPanel panel = new JPanel();
		String[] columns = {"Name", "Lineup Position", "Field Position"};
		List<Object[]> playersL = new ArrayList<Object[]>();
		LineupManager lm = LineupManager.teamImport("K", pool);
		for (String s : lm.getTeam().keySet()) {
			System.out.println(s);
			Object[] player = {s, null, null};
			playersL.add(player);
		}
		Object[][] players = playersL.toArray(new Object[playersL.size()][3]);
		JTable table = new JTable(players, columns);
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lm.export("K");
			}
		});
		table.setFont(f);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(1000, 1000);
		panel.add(scrollPane);
		panel.add(export);
		mainWindow.add(panel);
		return mainWindow;
	}
}
