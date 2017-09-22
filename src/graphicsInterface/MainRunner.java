//Remember to edit draft lists to show player data

package graphicsInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingx.JXList;

import gameData.*;
import players.PlayerComparator;
import players.PlayerData;
import players.Position;

public class MainRunner {

	private static LineupManager teamOne;
	private static LineupManager teamTwo;

	public static void main(String[] args) throws FileNotFoundException {
		DraftManager mainPool = DraftManager.initializePool(new File("2004 pitchers.txt"),
				new File("2004 hitters.txt"));
		Map<String, PlayerData> pool = mainPool.getPool();
		JFrame mainWindow = createMainFrame();
		Font standardF = new Font(Font.SANS_SERIF, Font.PLAIN, (mainWindow.getWidth() + mainWindow.getHeight()) / 200); // Arbitrary
		JFrame draftWindow = createDraftFrame(mainPool, standardF);
		JFrame poolWindow = createListFrame(pool, standardF);
		JFrame lineupWindow = createLineupFrame(pool, standardF);
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
	 * Creates the overall bottom level container uses in the rest of the
	 * interface
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
		poolList.setSize(500, 500);
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

	private static JPanel createDraftTeamPanel(DraftManager pool, LineupManager myTeam, Font f, JTextArea cardInfo,
			JXList poolList) {
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
		JButton draftButton = makeDraftButton(f, pool, poolList, myTeamStuff, myTeam);
		JButton otherButton = new JButton("Export Team");
		otherButton.addActionListener(makeDraftLineupExport(myTeamStuff, cloned));
		otherButton.setFont(f);
		myTeamStuff.setFont(f);
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
		String[] columns = { "Lineup Position", "Name", "Field Position" };
		List<Object[]> playersL = new ArrayList<Object[]>();
		LineupManager lm = LineupManager.teamImport("K", pool);
		for (String s : lm.getTeam().keySet()) {
			System.out.println(s);
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
		JTable table = new JTable(players, columns) {
			public boolean isCellEditable(int row, int column) {
				if (column != 1) {
					return true;
				}
				return true;
			}
		};
		table.setRowSelectionAllowed(false);
		JTextArea cardInfo = generateCardInfo(f);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (table.getSelectedColumn() == 1) {
					cardInfo.setText(
							pool.get(table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())).getCard());
				}
			}
		});
		table.getModel().addTableModelListener(makeLineupEditorListener(table));
		table.setRowHeight(f.getSize());
		JButton export = new JButton("Export");
		export.addActionListener(makeTableLineupExport(table, pool, lm));
		table.setFont(f);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(1000, 1000);
		panel.add(scrollPane);
		panel.add(export);
		panel.add(cardInfo, BorderLayout.EAST);
		mainWindow.add(panel);
		return mainWindow;
	}

	private static TableModelListener makeLineupEditorListener(JTable table) {
		return new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				table.getModel().removeTableModelListener(this);
				int column = table.getSelectedColumn();
				int row = table.getSelectedRow();
				int cellValue;
				if (column == 0) {
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

	private static ActionListener makeTableLineupExport(JTable table, Map<String, PlayerData> pool, LineupManager lm) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < table.getRowCount(); i++) {
					PlayerData p = (PlayerData) pool.get(table.getValueAt(i, 1));
					if (i < 9) {
						lm.hitInOrder(p.toString(), i + 1);
					}
					int f = Position.intFromAbbr((String) table.getValueAt(i, 2));
					if (f != -1) {
						lm.playTheField(p.toString(), f);
					}
				}
				lm.export("J");
			}
		};
	}
	
	private static ActionListener makeDraftLineupExport(JXList myTeamStuff, Map<String, PlayerData> cloned) {
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
				lm.export("Text area entry");
			}
		};
	}
	
	private static JButton makeDraftButton(Font f, DraftManager pool, JXList poolList, JXList myTeamStuff, LineupManager myTeam) {
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
}
