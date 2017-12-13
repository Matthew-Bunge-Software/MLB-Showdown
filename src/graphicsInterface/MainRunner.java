//Remember to edit draft lists to show player data

package graphicsInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXList;

import gameData.DraftManager;
import gameData.GameManager;
import gameData.LineupManager;
import gameData.StrategyCard;
import players.PlayerComparator;
import players.PlayerData;
import players.Position;

public class MainRunner {

	private static LineupManager teamOne;
	private static LineupManager teamTwo;
	private static Image background;
	private static Dimension screenSize;
	private static GameStartListener start;
	private static GameContinueListener gameContinue;
	private static GameManager game;
	private static Font f;
	private static Font boldF;
	private static JButton next;

	public static void main(String[] args) throws IOException, FileNotFoundException {
		StrategyCard scMan = new StrategyCard();
		teamOne = new LineupManager();
		teamTwo = new LineupManager();
		background = ImageIO.read(new File("DataFiles/BaseballField.jpg"));
		DraftManager mainPool = DraftManager.initializePool(new File("DataFiles/2004 pitchers.txt"),
				new File("DataFiles/2004 hitters.txt"));
		start = new GameStartListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				game = new GameManager(teamOne, teamTwo, scMan);
				gameContinue.registerItem(game);
			}
		};
		gameContinue = new GameContinueListener();
		start.registerItem(teamOne); start.registerItem(teamTwo);
		Map<String, PlayerData> pool = mainPool.getPool();
		start.registerItem(pool);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f = new Font(Font.SANS_SERIF, Font.PLAIN, (screenSize.width + screenSize.height) / 200); // Arbitrary
		boldF = new Font(Font.SANS_SERIF, Font.BOLD, (screenSize.width + screenSize.height) / 200); // Arbitrary
		JFrame mainWindow = createMainFrame(pool, screenSize, scMan);
		mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JFrame draftWindow = createDraftFrame(mainPool);
		JFrame poolWindow = createListFrame(pool);
		JFrame lineupWindow = createLineupFrame(pool);
		JMenuBar menuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("Main");
		JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener(new ActionListener() {
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
		newGame.setFont(f);
		load.setFont(f);
		poolButton.setFont(f);
		exit.setFont(f);
		mainMenu.add(newGame);
		mainMenu.add(load);
		mainMenu.add(poolButton);
		mainMenu.add(exit);
		mainMenu.setFont(f);
		menuBar.add(mainMenu);
		mainWindow.setJMenuBar(menuBar);
		mainWindow.setVisible(true);
	}

	/**
	 * Creates the overall top level container used in the rest of the interface
	 * Additionally sets up a blank game screen
	 * 
	 * @return The JFrame containing the overall base of the interface
	 */
	private static JFrame createMainFrame(Map<String, PlayerData> pool, Dimension screenSize, StrategyCard scMan) {	
		JFrame mainWindow = new JFrame("MLB Showdown");
		JPanel panelHome = new JPanel(new BorderLayout());
		JPanel panelAway = new JPanel(new BorderLayout());
		JPanel panelHomeStrat = makeStrategyPanel(teamOne);
		JPanel panelAwayStrat = makeStrategyPanel(teamTwo);
		JPanel panelHomeLineup = makeLineupPanel(pool, true);
		JPanel panelAwayLineup = makeLineupPanel(pool, true);
		JPanel centerPanel = new JPanel(new BorderLayout());
		next = new JButton("Start");
		next.setFont(f);
		start.registerItem(next, "next");
		JPanel mainPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, (int) (this.getWidth()), (int) (this.getHeight()), this);
				if (!next.getText().equals("Start")) {
					g.setColor(Color.BLACK);
					g.setFont(boldF);
					for (int i = 0; i < 10; i++) {
						if (i >= game.getGameStat().getAwaySpread().size()) {
							break;
						}
						g.drawString(Integer.toString(game.getGameStat().getAwaySpread().get(i)), (int) (this.getWidth() * (.26875 + i * .05375)), 
								(int) (this.getHeight() * .18));
						if (i >= game.getGameStat().getHomeSpread().size()) {
							break;
						}
						g.drawString(Integer.toString(game.getGameStat().getHomeSpread().get(i)), (int) (this.getWidth() * (.26875 + i * .05375)), 
								(int) (this.getHeight() * .2225));
					}
					g.drawString(Integer.toString(game.getGameStat().awayRuns), (int) (this.getWidth() * .825),
							(int) (this.getHeight() * .18));
					g.drawString(Integer.toString(game.getGameStat().homeRuns), (int) (this.getWidth() * .825),
							(int) (this.getHeight() * .2225));
				}
			}
		};
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (next.getText().equals("Start")) {
					start.actionPerformed(e);
				} else {
					gameContinue.actionPerformed(e);
				}
				mainPanel.repaint();
			}	
		});
		JTextArea belowMain = new JTextArea();
		gameContinue.registerItem(belowMain);
		JScrollPane belowMainPane = new JScrollPane(belowMain);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panelHomeLineup.setPreferredSize(new Dimension(0, (int) (screenSize.height *.7)));
		panelAwayLineup.setPreferredSize(new Dimension(0, (int) (screenSize.height * .7)));
		mainPanel.setPreferredSize(new Dimension(0, (int) (screenSize.height * .65)));
		belowMainPane.setPreferredSize(new Dimension(0, (int) (screenSize.height * .25)));
		belowMain.setFont(f);
		panelHome.add(panelHomeLineup, BorderLayout.NORTH);
		panelHome.add(panelHomeStrat, BorderLayout.SOUTH);
		panelAway.add(panelAwayLineup, BorderLayout.NORTH);
		panelAway.add(panelAwayStrat, BorderLayout.SOUTH);
		centerPanel.add(mainPanel, BorderLayout.NORTH);
		centerPanel.add(belowMainPane, BorderLayout.SOUTH);
		centerPanel.add(next, BorderLayout.CENTER);
		mainWindow.add(panelAway, BorderLayout.WEST);
		mainWindow.add(panelHome, BorderLayout.EAST);
		mainWindow.add(centerPanel, BorderLayout.CENTER);
		mainWindow.setSize(screenSize);
		return mainWindow;
	}
	
	private static JPanel makeStrategyPanel(LineupManager team) {
		JPanel panelStrat = new JPanel(new BorderLayout());
		JTextArea cardInfo = generateCardInfo();
		JXList cards = new JXList();
		JButton use = new JButton("Use");
		use.setEnabled(false);
		use.setFont(f);
		cards.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				StrategyCard sc = (StrategyCard) cards.getSelectedValue();
				if (!cards.isSelectionEmpty()) {
					cardInfo.setText(((StrategyCard) (cards.getSelectedValue())).getDescription());
					if (!StrategyCard.parsePrecondition(sc.getPre()) && use.getText() != "Discard") {
						use.setEnabled(false);
					} else {
						use.setEnabled(true);
					}
				}
			}
		});
		cards.setFont(f);
		JScrollPane scroller = new JScrollPane(cards);
		scroller.setViewportView(cards);
		scroller.setPreferredSize(new Dimension((int) (screenSize.width * .18), 250));
		cardInfo.setPreferredSize(new Dimension((int) (screenSize.width * .12), 250));
		cardInfo.setLineWrap(true);
		cardInfo.setWrapStyleWord(true);
		use.addActionListener(new ActionListener() {
			boolean discarding = false;
			int remaining;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DefaultListModel<StrategyCard> model = (DefaultListModel<StrategyCard>) cards.getModel();
				if (!discarding) {
					StrategyCard s = (StrategyCard) cards.getSelectedValue();
					String postCon = s.getPost();
					if (postCon.contains("DI+")) {
						String[] discard = postCon.split(" ");
						int discards = -1;
						for (String d : discard) {
							if (d.startsWith("DI")) {
								discards = Character.getNumericValue((d.charAt(d.length() - 1)));
							}
						}
						if (model.getSize() - 1 >= discards) {
							remaining = discards;
							discarding = true;
							use.setText("Discard");
							next.setEnabled(false);
						} else {
							cardInfo.setText("Not enough cards to discard");
							return;
						}
					}
					int index = cards.getSelectedIndex();
					cards.clearSelection();
					StrategyCard sc = model.remove(index);
					team.readyUse(sc);
					cards.setModel(model);
				} else {
					if (!cards.isSelectionEmpty()) {
						int index = cards.getSelectedIndex();
						cards.clearSelection();
						StrategyCard sc = model.remove(index);
						team.readyDiscard(sc);
						cards.setModel(model);
						remaining--;
						if (remaining == 0) {
							discarding = false;
							use.setText("Use");
							next.setEnabled(true);
						}
					}
				}
			}
			
		});
		panelStrat.add(use, BorderLayout.NORTH);
		panelStrat.add(scroller, BorderLayout.WEST);
		panelStrat.add(cardInfo, BorderLayout.EAST);
		panelStrat.setSize(500, 500);
		start.registerItem(cards);
		gameContinue.registerItem(cards);
		return panelStrat;
	}

	/**
	 * Creates the JFrame used to draft 2 separate teams
	 * 
	 * @param pool
	 *            The DraftManager for the particular pool of players being used
	 * @param f
	 *            The standard font being used across the project
	 * @return A JFrame composed of all the parts needed to draft players
	 */
	private static JFrame createDraftFrame(DraftManager pool) {
		JFrame draftWindow = new JFrame("Draft");
		JTextArea cardInfo = generateCardInfo();
		JXList poolList = generatePoolList(pool.getPool());
		poolList.setComparator(new PlayerComparator());
		poolList.setAutoCreateRowSorter(true);
		poolList.setSortOrder(SortOrder.ASCENDING);
		JPanel draftPanel = createDraftPanel(pool.getPool(), cardInfo, poolList);
		JPanel homeTeam = createDraftTeamPanel(pool, teamOne, cardInfo, poolList);
		JPanel awayTeam = createDraftTeamPanel(pool, teamTwo, cardInfo, poolList);
		homeTeam.setFont(f);
		awayTeam.setFont(f);
		draftWindow.add(draftPanel, BorderLayout.CENTER);
		draftWindow.add(homeTeam, BorderLayout.WEST);
		draftWindow.add(awayTeam, BorderLayout.EAST);
		draftWindow.setSize(2000, 1000);
		return draftWindow;
	}

	/**
	 * Creates an uneditable JTextArea. In partciular, this will be used to
	 * display card text
	 * 
	 * @param f
	 *            The standard font being used across the project
	 * @return an uneditable JTextArea
	 */
	private static JTextArea generateCardInfo() {
		JTextArea cardInfo = new JTextArea();
		cardInfo.setFont(f);
		cardInfo.setEditable(false);
		return cardInfo;
	}

	/**
	 * Creates a JXList of all players in a given Map of players
	 * 
	 * @param pool	A map of Strings of names to PlayerData of players who have said names
	 * @param f	The standard font being used across the project
	 * @return	A JXList of all players in pool
	 */
	private static JXList generatePoolList(Map<String, PlayerData> pool) {
		JXList poolList = new JXList();
		poolList.setSize(500, 500);
		DefaultListModel<PlayerData> lm = new DefaultListModel<PlayerData>();
		for (String s : pool.keySet()) {
			lm.addElement(pool.get(s));
		}
		poolList.setModel(lm);
		poolList.setFont(f);
		return poolList;
	}

	private static JPanel createDraftPanel(Map<String, PlayerData> pool, JTextArea cardInfo, JXList poolList) {
		JPanel draftPanel = new JPanel(new BorderLayout());
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

	private static JPanel createDraftTeamPanel(DraftManager pool, LineupManager myTeam, JTextArea cardInfo,
			JXList poolList) {
		Map<String, PlayerData> cloned = pool.getPool();
		JPanel draftPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JXList myTeamStuff = new JXList();
		JFileChooser fileName = new JFileChooser();
		fileName.setCurrentDirectory(new File(System.getProperty("user.dir")+"/SaveData"));
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
		JButton draftButton = makeDraftButton(pool, poolList, myTeamStuff, myTeam);
		JButton otherButton = new JButton("Export Team");
		otherButton.addActionListener(makeDraftLineupExport(myTeamStuff, cloned, fileName));
		otherButton.setFont(f);
		myTeamStuff.setFont(f);
		fileName.setFont(f);
		buttonPanel.add(draftButton, BorderLayout.SOUTH);
		buttonPanel.add(otherButton, BorderLayout.NORTH);
		draftPanel.add(poolScroller, BorderLayout.NORTH);
		draftPanel.add(buttonPanel, BorderLayout.SOUTH);
		return draftPanel;
	}

	private static JFrame createListFrame(Map<String, PlayerData> pool) {
		JFrame poolWindow = new JFrame("Draft Pool");
		JTextArea cardInfo = generateCardInfo();
		JXList poolList = generatePoolList(pool);
		poolList.setComparator(new PlayerComparator());
		poolList.setAutoCreateRowSorter(true);
		poolList.setSortOrder(SortOrder.ASCENDING);
		JPanel draftPanel = createDraftPanel(pool, cardInfo, poolList);
		poolWindow.add(draftPanel);
		draftPanel.setVisible(true);
		poolWindow.setSize(1000, 1000); // Arbitrary
		return poolWindow;
	}

	private static JFrame createLineupFrame(Map<String, PlayerData> pool) {
		JFrame mainWindow = new JFrame("Lineup Editor");
		JPanel panel = makeLineupPanel(pool, false);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//mainWindow.setSize((int) (screenSize.getWidth() * .3), (int) (screenSize.getHeight() * .5));
		mainWindow.add(panel);
		mainWindow.pack();
		return mainWindow;
	}
	
	private static JPanel makeLineupPanel(Map<String, PlayerData> pool, boolean main) {
		JPanel panel = new JPanel(new BorderLayout());
		JTable table = makeLineupTable();
		JTextArea cardInfo = generateCardInfo();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (table.getSelectedColumn() == 1) {
					cardInfo.setText(
							pool.get(table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())).getCard());
				}
			}
		});
		table.setRowHeight(f.getSize());
		JButton export = new JButton("Export");
		JButton importer = new JButton("Import");
		export.addActionListener(makeTableLineupExport(table, pool));
		importer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LineupManager lm = null;
				JFileChooser fileName = new JFileChooser();
				fileName.setFont(f);
				fileName.setCurrentDirectory(new File(System.getProperty("user.dir")+"/SaveData"));
				int returnValue = fileName.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					lm = LineupManager.teamImport(fileName.getSelectedFile().getName(), pool);
				}
				TableModel t = makeLineupTableModel(pool, lm);
				table.setModel(t);
				table.getModel().addTableModelListener(makeLineupEditorListener(table));
			}
		});
		table.getTableHeader().setFont(f);
		table.setFont(f);
		importer.setFont(f);
		export.setFont(f);
		JScrollPane scrollPane = new JScrollPane(table);
		JScrollPane cardInfoPane = new JScrollPane(cardInfo);
		panel.setPreferredSize(new Dimension((int) (screenSize.width * .3), (int) (screenSize.height * .3)));
		scrollPane.setPreferredSize(new Dimension((int) (screenSize.width * .18), (int) (screenSize.height)));
		cardInfoPane.setPreferredSize(new Dimension((int) (screenSize.width * .12), (int) (screenSize.height)));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(importer, BorderLayout.WEST);
		buttonPanel.add(export, BorderLayout.EAST);
		table.setFillsViewportHeight(true);
		table.setRowHeight(f.getSize());
		panel.add(scrollPane, BorderLayout.WEST);
		panel.add(cardInfoPane, BorderLayout.EAST);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		if (main) {
			start.registerItem(table); start.registerItem(export); start.registerItem(importer);
		}
		return panel;
	}

	private static TableModelListener makeLineupEditorListener(JTable table) {
		return new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				table.getModel().removeTableModelListener(this);
				int column = table.getSelectedColumn();
				int row = table.getSelectedRow();
				if (column == 0) {
					columnZeroModified(row, column, table);
				} else if (column == 2) {
					String s = (String) table.getValueAt(row, column);
					if (isInteger(s)) {
						table.setValueAt(Position.abbrFromInt(Integer.parseInt(s)), row, column);
					} else {
						if (Position.intFromAbbr(s) == 10) {
							table.setValueAt(null, row, column);
						}
					}
				}
				table.getModel().addTableModelListener(this);
			}
		};
	}

	private static ActionListener makeTableLineupExport(JTable table, Map<String, PlayerData> pool) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LineupManager lm = new LineupManager();
				for (int i = 0; i < table.getRowCount(); i++) {
					PlayerData p = (PlayerData) pool.get(table.getValueAt(i, 1));
					lm.addPlayer(p);
					if (i < 9) {
						lm.hitInOrder(p.toString(), i + 1);
					}
					int f = Position.intFromAbbr((String) table.getValueAt(i, 2));
					if (f != 10) {
						lm.playTheField(p.toString(), f);
					}
				}
				JFileChooser fileName = new JFileChooser();
				fileName.setFont(f);
				fileName.setCurrentDirectory(new File(System.getProperty("user.dir")+"/SaveData"));
				int returnValue = fileName.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					lm.export(fileName.getSelectedFile().getName());
				}
			}
		};
	}

	private static ActionListener makeDraftLineupExport(JXList myTeamStuff, Map<String, PlayerData> cloned,
			JFileChooser fileName) {
		return new ActionListener() {
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
				int returnValue = fileName.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					lm.export(fileName.getSelectedFile().getName());
				} else {
					throw new IllegalArgumentException("No file given");
				}
			}
		};
	}

	private static JTable makeLineupTable() {
		JTable table = new LineupTable();
		table.setRowSelectionAllowed(false);
		return table;
	}

	private static DefaultTableModel makeLineupTableModel(Map<String, PlayerData> pool, LineupManager lm) {
		String[] columns = { "Lineup Position", "Name", "Field Position" };
		List<Object[]> playersL = new ArrayList<Object[]>();
		for (String s : lm.getTeam().keySet()) {
			Object[] player = { lm.playerInLineup(s), s, Position.abbrFromInt(lm.playerInField(s)) };
			playersL.add(player);
		}
		playersL.sort(new Comparator<Object[]>() {
			@Override
			public int compare(Object[] arg0, Object[] arg1) {
				return ((Integer) arg0[0]).compareTo((Integer) arg1[0]);
			}
		});
		Object[][] players = playersL.toArray(new Object[playersL.size()][3]);
		for (int i = 0; i < players.length; i++) {
			if (i < 9) {
				players[i][0] = i + 1;
			} else {
				players[i][0] = null;
			}
			if (Position.intFromAbbr((String) players[i][2]) > 9) {
				players[i][2] = null;
			}
		}
		return new DefaultTableModel(players, columns);

	}

	private static JButton makeDraftButton(DraftManager pool, JXList poolList, JXList myTeamStuff,
			LineupManager myTeam) {
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
		draftButton.setFont(f);
		return draftButton;
	}

	private static boolean isInteger(String s) {
		if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '-')) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				if (i != 0 || s.charAt(i) != '-') {
					return false;
				}
			}
		}
		return true;
	}

	private static void columnZeroModified(int row, int column, JTable table) {
		int cellValue;
		String s = (String) table.getValueAt(row, column);
		if (isInteger(s)) {
			cellValue = Integer.parseInt(s);
		} else {
			cellValue = -1;
		}
		if (cellValue > 0 && cellValue < 10) {
			if (cellValue != row + 1) {
				Object temp = table.getValueAt(cellValue - 1, 1);
				Object temp2 = table.getValueAt(cellValue - 1, 2);
				table.setValueAt(table.getValueAt(row, 1), cellValue - 1, 1);
				table.setValueAt(table.getValueAt(row, 2), cellValue - 1, 2);
				table.setValueAt(temp, row, 1);
				table.setValueAt(temp2, row, 2);
			}
		}
		if (row < 9) {
			table.setValueAt("" + (row + 1), row, column);
		} else {
			table.setValueAt("", row, column);
		}
	}
}
