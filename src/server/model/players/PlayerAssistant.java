package server.model.players;

import server.Config;
import server.Connection;
import server.Server;
import server.core.PlayerHandler;
import server.model.npcs.NPCHandler;
import server.util.Misc;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

public class PlayerAssistant {

	private Client c;

	public PlayerAssistant(Client Client) {
		this.c = Client;
	}

	public int CraftInt, Dcolor, FletchInt;

	/**
	 * MulitCombat icon
	 * 
	 * @param i1
	 *            0 = off 1 = on
	 */
	public void multiWay(int i1) {
		c.outStream.createFrame(61);
		c.outStream.writeByte(i1);
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}

	/*
	 * Vengeance
	 */
	public void castVeng() {
		if (c.playerLevel[6] < 94) {
			c.sendMessage("You need a magic level of 94 to cast this spell.");
			return;
		}
		if (c.playerLevel[1] < 40) {
			c.sendMessage("You need a defence level of 40 to cast this spell.");
			return;
		}
		if (!c.getItems().playerHasItem(9075, 4)
				|| !c.getItems().playerHasItem(557, 10)
				|| !c.getItems().playerHasItem(560, 2)) {
			c.sendMessage("You don't have the required runes to cast this spell.");
			return;
		}
		if (System.currentTimeMillis() - c.lastCast < 30000) {
			c.sendMessage("You can only cast vengeance every 30 seconds.");
			return;
		}
		if (c.vengOn) {
			c.sendMessage("You already have vengeance casted.");
			return;
		}
		c.startAnimation(905);
		c.gfx100(666);// Just use c.gfx100
		c.getItems().deleteItem2(9075, 4);
		c.getItems().deleteItem2(557, 10);// For these you need to change to
											// deleteItem(item, itemslot,
											// amount);.
		c.getItems().deleteItem2(560, 2);
		addSkillXP(112, 6);
		refreshSkill(6);
		c.vengOn = true;
		c.lastCast = System.currentTimeMillis();
	}
	
	public void createProjectile3(int casterY, int casterX, int offsetY, int offsetX, int gfxMoving, int StartHeight, int endHeight, int speed, int AtkIndex) {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Client p = (Client) PlayerHandler.players[i];
				if (p.goodDistance(c.absX, c.absY, p.absX, p.absY, 60)) {
					if (p.heightLevel == c.heightLevel) {
						if (PlayerHandler.players[i] != null && !PlayerHandler.players[i].disconnected) {
							p.outStream.createFrame(85);
							p.outStream.writeByteC((casterY - (p.mapRegionY * 8)) - 2);
							p.outStream.writeByteC((casterX - (p.mapRegionX * 8)) - 3);
							p.outStream.createFrame(117);
							p.outStream.writeByte(50);
							p.outStream.writeByte(offsetY);
							p.outStream.writeByte(offsetX);
							p.outStream.writeWord(AtkIndex);
							p.outStream.writeWord(gfxMoving);
							p.outStream.writeByte(StartHeight);
							p.outStream.writeByte(endHeight);
							p.outStream.writeWord(51);
							p.outStream.writeWord(speed);
							p.outStream.writeByte(16);
							p.outStream.writeByte(64);
						}
					}
				}
			}
		}
	}
	public void sendNewString(String s, int id) {
		if (c != null) {
			c.getStringMap().put(id, s);
		}
	}

	/*public void getDragonClawHits(Client c, int i) {
		c.clawHit[0] = i+Misc.random(10)+1;
		c.clawHit[1] = c.clawHit[0]/2;
		c.clawHit[2] = c.clawHit[1]/2;
		c.clawHit[3] = (c.clawHit[1]-c.clawHit[2]);
		c.sendMessage(""+c.clawHit[0]+","+c.clawHit[1]+","+c.clawHit[2]+","+c.clawHit[3]+".");
	}

	public void hitDragonClaws(final Client c, int damage) {
		if(!c.usingClaws) {
			return;
		}
		if(c.clawHit[0] <= 0) {
			getDragonClawHits(c, damage);
		}
		if(c.npcIndex > 0) {
			c.getCombat().applyNpcMeleeDamage(c.npcIndex, 1, c.clawHit[0]);
			c.getCombat().applyNpcMeleeDamage(c.npcIndex, 2, c.clawHit[1]);
		} else if(c.playerIndex > 0) {
			c.getCombat().applyPlayerMeleeDamage(c.playerIndex, 1, c.clawHit[0]);
			c.getCombat().applyPlayerMeleeDamage(c.playerIndex, 2, c.clawHit[1]);
		}
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if(c.npcIndex > 0) {
					c.getCombat().applyNpcMeleeDamage(c.npcIndex, 1, c.clawHit[2]);
					c.getCombat().applyNpcMeleeDamage(c.npcIndex, 2, c.clawHit[3]);
				} else if(c.playerIndex > 0) {
					c.getCombat().applyPlayerMeleeDamage(c.playerIndex, 1, c.clawHit[2]);
					c.getCombat().applyPlayerMeleeDamage(c.playerIndex, 2, c.clawHit[3]);
				}
				resetDragonHits(c);
				container.stop();
			}
			@Override
			public void stop() {

			}
		}, 1);
	}

	public void resetDragonHits(Client c) {
		for(int i = 0; i < 4; i++) {
			c.clawHit[i] = -1;
		}
		c.usingClaws = false;
	}*/
	
	public void sendStatement(String s) {
		c.getPA().sendFrame126(s, 357);
		c.getPA().sendFrame126("Click here to continue", 358);
		c.getPA().sendFrame164(356);
	}
	
	public void sendStatement2(String s, String s2) {
		c.getPA().sendFrame126(s, 357);
		c.getPA().sendFrame126(s2, 358);
		c.getPA().sendFrame126("Click here to continue", 359);
		c.getPA().sendFrame164(356);
	}

	public void clearClanChat() {
		c.clanId = -1;
		c.getPA().sendFrame126("Talking in: ", 18139);
		c.getPA().sendFrame126("Owner: ", 18140);
		for (int j = 18144; j < 18244; j++)
			c.getPA().sendFrame126("", j);
	}
	
	 public void playerWalk(int x, int y) {
         PathFinder.getPathFinder().findRoute(c, x, y, true, 1, 1);
 }

	public void resetAutocast() {
		c.autocastId = 0;
		c.autocasting = false;
		c.getPA().sendFrame36(108, 0);
	}

	public void sendFrame126(String s, int id) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(126);
			c.getOutStream().writeString(s);
			c.getOutStream().writeWordA(id);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}
	
	public void handleObjectRegion(int objectId, int minX, int minY, int maxX, int maxY) {
		for (int i = minX; i < maxX+1; i++) {
			for (int j = minY; j < maxY+1; j++) {
				c.getPA().object(objectId, i, j, -1, 10);
			}
		}
	}
	
	public boolean itemUsedInRegion(int minX, int maxX, int minY, int maxY) {
		return (c.objectX >= minX && c.objectX <= maxX) && (c.objectY >= minY && c.objectY <= maxY);
	}

	public void sendLink(String s) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(187);
			c.getOutStream().writeString(s);
		}
	}

	public void setSkillLevel(int skillNum, int currentLevel, int XP) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(134);
			c.getOutStream().writeByte(skillNum);
			c.getOutStream().writeDWord_v1(XP);
			c.getOutStream().writeByte(currentLevel);
			c.flushOutStream();
		}
	}

	public void sendFrame106(int sideIcon) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(106);
			c.getOutStream().writeByteC(sideIcon);
			c.flushOutStream();
			requestUpdates();
		}
	}

	public void sendFrame107() {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(107);
			c.flushOutStream();
		}
	}

	public void sendFrame36(int id, int state) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(36);
			c.getOutStream().writeWordBigEndian(id);
			c.getOutStream().writeByte(state);
			c.flushOutStream();
		}
	}

	public void sendFrame185(int Frame) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(185);
			c.getOutStream().writeWordBigEndianA(Frame);
		}
	}

	public void showInterface(int interfaceid) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(97);
			c.getOutStream().writeWord(interfaceid);
			c.flushOutStream();
		}
	}

	public void sendFrame248(int MainFrame, int SubFrame) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(246);
			c.getOutStream().writeWordBigEndian(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.getOutStream().writeWord(SubFrame2);
			c.flushOutStream();
		}
	}

	public void sendFrame171(int MainFrame, int SubFrame) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(171);
			c.getOutStream().writeByte(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame200(int MainFrame, int SubFrame) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(200);
			c.getOutStream().writeWord(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame70(int i, int o, int id) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(70);
			c.getOutStream().writeWord(i);
			c.getOutStream().writeWordBigEndian(o);
			c.getOutStream().writeWordBigEndian(id);
			c.flushOutStream();
		}
	}

	public void sendFrame75(int MainFrame, int SubFrame) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(75);
			c.getOutStream().writeWordBigEndianA(MainFrame);
			c.getOutStream().writeWordBigEndianA(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame164(int Frame) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(164);
			c.getOutStream().writeWordBigEndian_dup(Frame);
			c.flushOutStream();
		}
	}

	public void setPrivateMessaging(int i) { // friends and ignore list status
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(221);
			c.getOutStream().writeByte(i);
			c.flushOutStream();
		}
	}

	public void setChatOptions(int publicChat, int privateChat, int tradeBlock) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(206);
			c.getOutStream().writeByte(publicChat);
			c.getOutStream().writeByte(privateChat);
			c.getOutStream().writeByte(tradeBlock);
			c.flushOutStream();
		}
	}

	public void sendFrame87(int id, int state) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(87);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.getOutStream().writeDWord_v1(state);
			c.flushOutStream();
		}
	}

	public void sendPM(long name, int rights, byte[] chatmessage,
			int messagesize) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSize(196);
			c.getOutStream().writeQWord(name);
			c.getOutStream().writeDWord(c.lastChatId++);
			c.getOutStream().writeByte(rights);
			c.getOutStream().writeBytes(chatmessage, messagesize, 0);
			c.getOutStream().endFrameVarSize();
			c.flushOutStream();
			Misc.textUnpack(chatmessage, messagesize);
			Misc.longToPlayerName(name);
		}
	}

	public void createPlayerHints(int type, int id) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(254);
			c.getOutStream().writeByte(type);
			c.getOutStream().writeWord(id);
			c.getOutStream().write3Byte(0);
			c.flushOutStream();
		}
	}

	public void createObjectHints(int x, int y, int height, int pos) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(254);
			c.getOutStream().writeByte(pos);
			c.getOutStream().writeWord(x);
			c.getOutStream().writeWord(y);
			c.getOutStream().writeByte(height);
			c.flushOutStream();
		}
	}

	public void loadPM(long playerName, int world) {
		if (c.getOutStream() != null && c != null) {
			if (world != 0) {
				world += 9;
			} else if (!Config.WORLD_LIST_FIX) {
				world += 1;
			}
			c.getOutStream().createFrame(50);
			c.getOutStream().writeQWord(playerName);
			c.getOutStream().writeByte(world);
			c.flushOutStream();
		}
	}

	public void removeAllWindows() {
		if (c.getOutStream() != null && c != null) {
			c.getPA().resetVariables();
			c.getOutStream().createFrame(219);
			c.flushOutStream();
		}
	}

	public void closeAllWindows() {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(219);
			c.flushOutStream();
		}
	}

	public void sendFrame34(int id, int slot, int column, int amount) {
		if (c.getOutStream() != null && c != null) {
			c.outStream.createFrameVarSizeWord(34); // init item to smith
													// screen
			c.outStream.writeWord(column); // Column Across Smith Screen
			c.outStream.writeByte(4); // Total Rows?
			c.outStream.writeDWord(slot); // Row Down The Smith Screen
			c.outStream.writeWord(id + 1); // item
			c.outStream.writeByte(amount); // how many there are?
			c.outStream.endFrameVarSizeWord();
		}
	}

	public void walkableInterface(int id) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(208);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.flushOutStream();
		}
	}

	public int mapStatus = 0;

	public void sendFrame99(int state) { // used for disabling map
		if (c.getOutStream() != null && c != null) {
			if (mapStatus != state) {
				mapStatus = state;
				c.getOutStream().createFrame(99);
				c.getOutStream().writeByte(state);
				c.flushOutStream();
			}
		}
	}

	/*public void sendCrashFrame() { // used for crashing cheat clients
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(123);
			c.flushOutStream();
		}
	}*/

	/**
	 * Reseting animations for everyone
	 **/

	public void frame1() {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Client person = (Client) PlayerHandler.players[i];
				if (person != null) {
					if (person.getOutStream() != null && !person.disconnected) {
						if (c.distanceToPoint(person.getX(), person.getY()) <= 25) {
							person.getOutStream().createFrame(1);
							person.flushOutStream();
							person.getPA().requestUpdates();
						}
					}
				}
			}
		}
	}

	
	
	public void itemOnInterface(int interfaceChild, int zoom, int itemId) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(246);
			c.getOutStream().writeWordBigEndian(interfaceChild);
			c.getOutStream().writeWord(zoom);
			c.getOutStream().writeWord(itemId);
			c.flushOutStream();
		}
	}	
	
	public static void showInterface(Client client, int i) {
		client.getOutStream().createFrame(97);
		client.getOutStream().writeWord(i);
		client.flushOutStream();
	}
	
	/**
	 * Creating projectile
	 **/
	public void createProjectile(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
			c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
			c.getOutStream().createFrame(117);
			c.getOutStream().writeByte(angle);
			c.getOutStream().writeByte(offY);
			c.getOutStream().writeByte(offX);
			c.getOutStream().writeWord(lockon);
			c.getOutStream().writeWord(gfxMoving);
			c.getOutStream().writeByte(startHeight);
			c.getOutStream().writeByte(endHeight);
			c.getOutStream().writeWord(time);
			c.getOutStream().writeWord(speed);
			c.getOutStream().writeByte(16);
			c.getOutStream().writeByte(64);
			c.flushOutStream();
		}
	}

	public void createProjectile2(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time, int slope) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
			c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
			c.getOutStream().createFrame(117);
			c.getOutStream().writeByte(angle);
			c.getOutStream().writeByte(offY);
			c.getOutStream().writeByte(offX);
			c.getOutStream().writeWord(lockon);
			c.getOutStream().writeWord(gfxMoving);
			c.getOutStream().writeByte(startHeight);
			c.getOutStream().writeByte(endHeight);
			c.getOutStream().writeWord(time);
			c.getOutStream().writeWord(speed);
			c.getOutStream().writeByte(slope);
			c.getOutStream().writeByte(64);
			c.flushOutStream();
		}
	}

	// projectiles for everyone within 25 squares
	public void createPlayersProjectile(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Client person = (Client) p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							if (p.heightLevel == c.heightLevel)
								person.getPA().createProjectile(x, y, offX,
										offY, angle, speed, gfxMoving,
										startHeight, endHeight, lockon, time);
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile2(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time, int slope) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Client person = (Client) p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							person.getPA().createProjectile2(x, y, offX, offY,
									angle, speed, gfxMoving, startHeight,
									endHeight, lockon, time, slope);
						}
					}
				}
			}
		}
	}

	/**
	 ** GFX
	 **/
	public void stillGfx(int id, int x, int y, int height, int time) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(y - (c.getMapRegionY() * 8));
			c.getOutStream().writeByteC(x - (c.getMapRegionX() * 8));
			c.getOutStream().createFrame(4);
			c.getOutStream().writeByte(0);
			c.getOutStream().writeWord(id);
			c.getOutStream().writeByte(height);
			c.getOutStream().writeWord(time);
			c.flushOutStream();
		}
	}

	// creates gfx for everyone
	public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Client person = (Client) p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							person.getPA().stillGfx(id, x, y, height, time);
						}
					}
				}
			}
		}
	}

	/**
	 * Objects, add and remove
	 **/
	public void object(int objectId, int objectX, int objectY, int face,
			int objectType) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
			c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
			c.getOutStream().createFrame(101);
			c.getOutStream().writeByteC((objectType << 2) + (face & 3));
			c.getOutStream().writeByte(0);

			if (objectId != -1) { // removing
				c.getOutStream().createFrame(151);
				c.getOutStream().writeByteS(0);
				c.getOutStream().writeWordBigEndian(objectId);
				c.getOutStream().writeByteS((objectType << 2) + (face & 3));
			}
			c.flushOutStream();
		}
	}

	public void checkObjectSpawn(int objectId, int objectX, int objectY,
			int face, int objectType) {
		if (c.distanceToPoint(objectX, objectY) > 60)
			return;
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
			c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
			c.getOutStream().createFrame(101);
			c.getOutStream().writeByteC((objectType << 2) + (face & 3));
			c.getOutStream().writeByte(0);

			if (objectId != -1) { // removing
				c.getOutStream().createFrame(151);
				c.getOutStream().writeByteS(0);
				c.getOutStream().writeWordBigEndian(objectId);
				c.getOutStream().writeByteS((objectType << 2) + (face & 3));
			}
			c.flushOutStream();
		}
	}

	/**
	 * Show option, attack, trade, follow etc
	 **/
	public String optionType = "null";

	public void showOption(int i, int l, String s, int a) {
		if (c.getOutStream() != null && c != null) {
			if (!optionType.equalsIgnoreCase(s)) {
				optionType = s;
				c.getOutStream().createFrameVarSize(104);
				c.getOutStream().writeByteC(i);
				c.getOutStream().writeByteA(l);
				c.getOutStream().writeString(s);
				c.getOutStream().endFrameVarSize();
				c.flushOutStream();
			}
		}
	}

	/**
	 * Open bank
	 **/
	public void openUpBank() {
		if (c.getOutStream() != null && c != null) {
			c.getItems().resetItems(5064);
			c.getItems().rearrangeBank();
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(5292);
			c.getOutStream().writeWord(5063);
			c.flushOutStream();
		}
	}

	/**
	 * Private Messaging
	 **/
	public void logIntoPM() {
		setPrivateMessaging(2);
		for (int i1 = 0; i1 < Config.MAX_PLAYERS; i1++) {
			Player p = PlayerHandler.players[i1];
			if (p != null && p.isActive) {
				Client o = (Client) p;
				if (o != null) {
					o.getPA().updatePM(c.playerId, 1);
				}
			}
		}
		boolean pmLoaded = false;

		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] != 0) {
				for (int i2 = 1; i2 < Config.MAX_PLAYERS; i2++) {
					Player p = PlayerHandler.players[i2];
					if (p != null
							&& p.isActive
							&& Misc.playerNameToInt64(p.playerName) == c.friends[i]) {
						Client o = (Client) p;
						if (o != null) {
							if (c.playerRights >= 2
									|| p.privateChat == 0
									|| (p.privateChat == 1 && o
											.getPA()
											.isInPM(Misc
													.playerNameToInt64(c.playerName)))) {
								loadPM(c.friends[i], 1);
								pmLoaded = true;
							}
							break;
						}
					}
				}
				if (!pmLoaded) {
					loadPM(c.friends[i], 0);
				}
				pmLoaded = false;
			}
			for (int i1 = 1; i1 < Config.MAX_PLAYERS; i1++) {
				Player p = PlayerHandler.players[i1];
				if (p != null && p.isActive) {
					Client o = (Client) p;
					if (o != null) {
						o.getPA().updatePM(c.playerId, 1);
					}
				}
			}
		}
	}

	public void updatePM(int pID, int world) { // used for private chat updates
		Player p = PlayerHandler.players[pID];
		if (p == null || p.playerName == null || p.playerName.equals("null")) {
			return;
		}
		Client o = (Client) p;
		long l = Misc
				.playerNameToInt64(PlayerHandler.players[pID].playerName);

		if (p.privateChat == 0) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i]) {
						loadPM(l, world);
						return;
					}
				}
			}
		} else if (p.privateChat == 1) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i]) {
						if (o.getPA().isInPM(
								Misc.playerNameToInt64(c.playerName))) {
							loadPM(l, world);
							return;
						} else {
							loadPM(l, 0);
							return;
						}
					}
				}
			}
		} else if (p.privateChat == 2) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i] && c.playerRights < 2) {
						loadPM(l, 0);
						return;
					}
				}
			}
		}
	}

	public boolean isInPM(long l) {
		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] != 0) {
				if (l == c.friends[i]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Drink AntiPosion Potions
	 * 
	 * @param itemId
	 *            The itemId
	 * @param itemSlot
	 *            The itemSlot
	 * @param newItemId
	 *            The new item After Drinking
	 * @param healType
	 *            The type of poison it heals
	 */

	public void potionPoisonHeal(int itemId, int itemSlot, int newItemId,
			int healType) {
		c.attackTimer = c.getCombat().getAttackDelay(
				c.getItems().getItemName(c.playerEquipment[c.playerWeapon])
						.toLowerCase());
		if (c.duelRule[5]) {
			c.sendMessage("Potions has been disabled in this duel!");
			return;
		}
		if (!c.isDead && System.currentTimeMillis() - c.foodDelay > 2000) {
			if (c.getItems().playerHasItem(itemId, 1, itemSlot)) {
				c.sendMessage("You drink the "
						+ c.getItems().getItemName(itemId).toLowerCase() + ".");
				c.foodDelay = System.currentTimeMillis();
				// Actions
				if (healType == 1) {
					// Cures The Poison
				} else if (healType == 2) {
					// Cures The Poison + protects from getting poison again
				}
				c.startAnimation(0x33D);
				c.getItems().deleteItem(itemId, itemSlot, 1);
				c.getItems().addItem(newItemId, 1);
				requestUpdates();
			}
		}
	}

	/**
	 * Magic on items
	 **/
	
	public int[][] Enchant = {{1694,1727},{1637,2550},{1656,3853},//sapphire
			{1696,1729},{1639,2552},{1658,5521},//emerald
			{1641,2568},{1698,1725},{1660,11194},//ruby
			{1643,2570},{1700,1731},{1662,11090},//diamond
			{1645,2572},{1702,1712},{1664,11105},//dragonstone
			{6575,6583},{6577,11128},{6581,6585}}; //onyx
	//rune, amount, level, xp, rune2
	public int[][] RunesAndXp = {{555,1,7,9,-1},{555,1,7,9,-1},{555,1,7,9,-1},//sapphire
			{556,3,27,15,-1},{556,3,27,15,-1},{556,3,27,15,-1},//emerald
			{554,5,49,28,-1},{554,5,49,28,-1},{554,5,49,28,-1},//ruby
			{557,10,57,32,-1},{557,10,57,32,-1},{557,10,57,32,-1},//diamond
			{557,15,68,38,555},{557,15,68,46,555},{557,15,68,46,555},//dragonstone
			{557,15,87,48,554},{557,15,87,48,554},{557,15,87,48,554}}; //onyx
		 
	public int item, item2, rune, amount, level, xp, rune2;

	public void magicOnItems(int slot, int itemId, int spellId) {
		if (!c.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}

		switch (spellId) {
		case 1162: // low alch
			if (System.currentTimeMillis() - c.alchDelay > 1000) {
				if (!c.getCombat().checkMagicReqs(49)) {
					break;
				}
				if (itemId == 995) {
					c.sendMessage("You can't alch coins");
					break;
				}
				c.getItems().deleteItem(itemId, slot, 1);
				c.getItems().addItem(995,
						c.getShops().getItemShopValue(itemId) / 3);
				c.startAnimation(c.MAGIC_SPELLS[49][2]);
				c.gfx100(c.MAGIC_SPELLS[49][3]);
				c.alchDelay = System.currentTimeMillis();
				sendFrame106(6);
				addSkillXP(c.MAGIC_SPELLS[49][7] * Config.MAGIC_EXP_RATE, 6);
				refreshSkill(6);
			}
			break;
			
		case 1155: 
		case 1165:
		case 1176:
		case 1180:
		case 1187:
		case 6003:
			for (int j = 0; j < Enchant.length; j++) {
				for (int k = 0; k < RunesAndXp.length; k++) {	
				if (Enchant[j][0] == itemId){
					item = Enchant[j][0];
					item2 = Enchant[j][1];
					rune = RunesAndXp[k][0];
					rune2 = RunesAndXp[k][4];
					amount = RunesAndXp[k][1];
					level = RunesAndXp[k][2];
					xp = RunesAndXp[k][3];
				if (c.playerLevel[6] >= level){
					if (c.getItems().playerHasItem(rune, amount)){
						if (!c.getItems().playerHasItem(rune2, amount)){
							if (rune2 == -1)
								continue;
							else {
								c.sendMessage("You do not have all the required rune to cast this spell.");
								break;
						}
						}
						if (rune2 > -1)
						c.getItems().deleteItem(rune2, 1);
						c.startAnimation(712, 6);
						c.gfx100(114);
						c.getItems().deleteItem(item, slot, 1);
						c.getItems().addItem(item2, 1);
						c.getItems().deleteItem(rune, 1);
						c.getItems().deleteItem(564, 1);
						sendFrame106(6);
						addSkillXP(xp * Config.MAGIC_EXP_RATE, 6);
						refreshSkill(6);
						break;
					}else{
						c.sendMessage("you do not have the required runes to cast this spell");
					}
				}else{
					c.sendMessage("you need a magic level of "+level+" to cast this spell." );
				}
				}	
			}
			}
				break;

		case 1178: // high alch
			if (System.currentTimeMillis() - c.alchDelay > 2000) {
				if (!c.getCombat().checkMagicReqs(50)) {
					break;
				}
				if (itemId == 995) {
					c.sendMessage("You can't alch coins");
					break;
				}
				c.getItems().deleteItem(itemId, slot, 1);
				c.getItems().addItem(995,
						(int) (c.getShops().getItemShopValue(itemId) * .75));
				c.startAnimation(c.MAGIC_SPELLS[50][2]);
				c.gfx100(c.MAGIC_SPELLS[50][3]);
				c.alchDelay = System.currentTimeMillis();
				sendFrame106(6);
				addSkillXP(c.MAGIC_SPELLS[50][7] * Config.MAGIC_EXP_RATE, 6);
				refreshSkill(6);
			}
			break;
		}
	}

	/**
	 * Dieing
	 **/

	public void applyDead() {
		c.respawnTimer = 15;
		c.isDead = false;

		if (c.duelStatus != 6) {
			// c.killerId = c.getCombat().getKillerId(c.playerId);
			c.killerId = findKiller();
			Client o = (Client) PlayerHandler.players[c.killerId];
			if (o != null) {
				if (c.killerId != c.playerId)
					o.sendMessage("You have defeated " + c.playerName + "!");
				if (o.duelStatus == 5) {
					o.duelStatus++;
				}
			}
		}
		c.faceUpdate(0);
		c.npcIndex = 0;
		c.playerIndex = 0;
		c.stopMovement();
		if (c.duelStatus <= 4) {
			c.sendMessage("Oh dear you are dead!");
		} else if (c.duelStatus != 6) {
			c.sendMessage("You have lost the duel!");
		}
		resetDamageDone();
		c.specAmount = 10;
		c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
		c.lastVeng = 0;
		c.vengOn = false;
		resetFollowers();
		c.attackTimer = 10;
		removeAllWindows();
		c.tradeResetNeeded = true;
	}

	public void resetDamageDone() {
		for (int i = 0; i < PlayerHandler.players.length; i++) {
			if (PlayerHandler.players[i] != null) {
				PlayerHandler.players[i].damageTaken[c.playerId] = 0;
			}
		}
	}
	
	

	public void vengMe() {
		if (System.currentTimeMillis() - c.lastVeng > 30000) {
			if (c.getItems().playerHasItem(557, 10)
					&& c.getItems().playerHasItem(9075, 4)
					&& c.getItems().playerHasItem(560, 2)) {
				c.vengOn = true;
				c.lastVeng = System.currentTimeMillis();
				c.startAnimation(4410);
				c.gfx100(726);
				c.getItems().deleteItem(557, c.getItems().getItemSlot(557), 10);
				c.getItems().deleteItem(560, c.getItems().getItemSlot(560), 2);
				c.getItems()
						.deleteItem(9075, c.getItems().getItemSlot(9075), 4);
			} else {
				c.sendMessage("You do not have the required runes to cast this spell. (9075 for astrals)");
			}
		} else {
			c.sendMessage("You must wait 30 seconds before casting this again.");
		}
	}

	public void resetTb() {
		c.teleBlockLength = 0;
		c.teleBlockDelay = 0;
	}

	public void resetFollowers() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].followId == c.playerId) {
					Client c = (Client) PlayerHandler.players[j];
					c.getPA().resetFollow();
				}
			}
		}
	}

	public void giveLife() {
		c.isDead = false;
		c.faceUpdate(-1);
		c.freezeTimer = 0;
		if (c.duelStatus <= 4 && !c.getPA().inPitsWait()) {
			if (!c.inPits && !c.inFightCaves()) {
				c.getItems().resetKeepItems();
				if ((c.playerRights == 2 && Config.ADMIN_DROP_ITEMS)
						|| c.playerRights != 2) {
					if (!c.isSkulled) { // what items to keep
						c.getItems().keepItem(0, true);
						c.getItems().keepItem(1, true);
						c.getItems().keepItem(2, true);
					}
					if (c.prayerActive[10]
							&& System.currentTimeMillis() - c.lastProtItem > 700) {
						c.getItems().keepItem(3, true);
					}
					c.getItems().dropAllItems(); // drop all items
					c.getItems().deleteAllItems(); // delete all items

					if (!c.isSkulled) { // add the kept items once we finish
										// deleting and dropping them
						for (int i1 = 0; i1 < 3; i1++) {
							if (c.itemKeptId[i1] > 0) {
								c.getItems().addItem(c.itemKeptId[i1], 1);
							}
						}
					}
					if (c.prayerActive[10]) { // if we have protect items
						if (c.itemKeptId[3] > 0) {
							c.getItems().addItem(c.itemKeptId[3], 1);
						}
					}
				}
				c.getItems().resetKeepItems();
			} else if (c.inPits) {
				Server.fightPits.removePlayerFromPits(c.playerId);
				c.pitsStatus = 1;
			}
		}
		c.getCombat().resetPrayers();
		for (int i = 0; i < 20; i++) {
			c.playerLevel[i] = getLevelForXP(c.playerXP[i]);
			c.getPA().refreshSkill(i);
		}
		if (c.pitsStatus == 1) {
			movePlayer(2399, 5173, 0);
		} else if (c.duelStatus <= 4) { // if we are not in a duel repawn to
										// wildy
			movePlayer(Config.RESPAWN_X, Config.RESPAWN_Y, 0);
			c.isSkulled = false;
			c.skullTimer = 0;
			c.attackedPlayers.clear();
		} else if (c.inFightCaves()) {
			c.getPA().resetTzhaar();
		} else { // we are in a duel, respawn outside of arena
			Client o = (Client) PlayerHandler.players[c.duelingWith];
			if (o != null) {
				o.getPA().createPlayerHints(10, -1);
				if (o.duelStatus == 6) {
					o.getTradeAndDuel().duelVictory();
				}
			}
			movePlayer(
					Config.DUELING_RESPAWN_X
							+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
					Config.DUELING_RESPAWN_Y
							+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)), 0);
			if (c.duelStatus != 6) { // if we have won but have died, don't
										// reset the duel status.
				c.getTradeAndDuel().resetDuel();
			}
		}
		// PlayerSaving.getSingleton().requestSave(c.playerId);
		PlayerSave.saveGame(c);
		c.getCombat().resetPlayerAttack();
		resetAnimation();
		c.startAnimation(65535);
		frame1();
		resetTb();
		c.isSkulled = false;
		c.attackedPlayers.clear();
		c.headIconPk = -1;
		c.skullTimer = -1;
		c.damageTaken = new int[Config.MAX_PLAYERS];
		c.getPA().requestUpdates();
		removeAllWindows();
		c.tradeResetNeeded = true;
	}

	/**
	 * Location change for digging, levers etc
	 **/

	public void changeLocation() {
		switch (c.newLocation) {
		case 1:
			sendFrame99(2);
			movePlayer(3578, 9706, -1);
			break;
		case 2:
			sendFrame99(2);
			movePlayer(3568, 9683, -1);
			break;
		case 3:
			sendFrame99(2);
			movePlayer(3557, 9703, -1);
			break;
		case 4:
			sendFrame99(2);
			movePlayer(3556, 9718, -1);
			break;
		case 5:
			sendFrame99(2);
			movePlayer(3534, 9704, -1);
			break;
		case 6:
			sendFrame99(2);
			movePlayer(3546, 9684, -1);
			break;
		}
		c.newLocation = 0;
	}

	/**
	 * Teleporting
	 **/
	public void spellTeleport(int x, int y, int height) {
		c.getPA().startTeleport(x, y, height,
				c.playerMagicBook == 1 ? "ancient" : "modern");
	}

	public void startTeleport(int x, int y, int height, String teleportType) {
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return;
		}
		if (c.inWild() && c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL) {
			c.sendMessage("You can't teleport above level "
					+ Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			if (c.playerIndex > 0 || c.npcIndex > 0)
				c.getCombat().resetPlayerAttack();
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			if (teleportType.equalsIgnoreCase("modern")) {
				c.startAnimation(714);
				c.teleTimer = 11;
				c.teleGfx = 308;
				c.teleEndAnimation = 715;
			}
			if (teleportType.equalsIgnoreCase("ancient")) {
				c.startAnimation(1979);
				c.teleGfx = 0;
				c.teleTimer = 9;
				c.teleEndAnimation = 0;
				c.gfx0(392);
			}

		}
	}

	public void startTeleport2(int x, int y, int height) {
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0) {
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.startAnimation(714);
			c.teleTimer = 11;
			c.teleGfx = 308;
			c.teleEndAnimation = 715;

		}
	}

	public void processTeleport() {
		c.teleportToX = c.teleX;
		c.teleportToY = c.teleY;
		c.heightLevel = c.teleHeight;
		if (c.teleEndAnimation > 0) {
			c.startAnimation(c.teleEndAnimation);
		}
	}

	public void movePlayer(int x, int y, int h) {
		c.resetWalkingQueue();
		c.teleportToX = x;
		c.teleportToY = y;
		c.heightLevel = h;
		requestUpdates();
	}

	/**
	 * Following
	 **/

	/*
	 * public void Player() { if(Server.playerHandler.players[c.followId] ==
	 * null || Server.playerHandler.players[c.followId].isDead) {
	 * c.getPA().resetFollow(); return; } if(c.freezeTimer > 0) { return; } int
	 * otherX = Server.playerHandler.players[c.followId].getX(); int otherY =
	 * Server.playerHandler.players[c.followId].getY(); boolean withinDistance =
	 * c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2); boolean
	 * hallyDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
	 * boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(),
	 * 6); boolean rangeWeaponDistance = c.goodDistance(otherX, otherY,
	 * c.getX(), c.getY(), 2); boolean sameSpot = (c.absX == otherX && c.absY ==
	 * otherY); if(!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
	 * c.followId = 0; c.getPA().resetFollow(); return; }
	 * c.faceUpdate(c.followId+32768); if ((c.usingBow || c.mageFollow ||
	 * c.autocastId > 0 && (c.npcIndex > 0 || c.playerIndex > 0)) && bowDistance
	 * && !sameSpot) { c.stopMovement(); return; } if (c.usingRangeWeapon &&
	 * rangeWeaponDistance && !sameSpot && (c.npcIndex > 0 || c.playerIndex >
	 * 0)) { c.stopMovement(); return; } if(c.goodDistance(otherX, otherY,
	 * c.getX(), c.getY(), 1) && !sameSpot) { return; }
	 * c.outStream.createFrame(174); boolean followPlayer = c.followId > 0; if
	 * (c.freezeTimer <= 0) if (followPlayer) c.outStream.writeWord(c.followId);
	 * else c.outStream.writeWord(c.followId2); else c.outStream.writeWord(0);
	 * 
	 * if (followPlayer) c.outStream.writeByte(1); else
	 * c.outStream.writeByte(0); if (c.usingBow && c.playerIndex > 0)
	 * c.followDistance = 5; else if (c.usingRangeWeapon && c.playerIndex > 0)
	 * c.followDistance = 3; else if (c.spellId > 0 && c.playerIndex > 0)
	 * c.followDistance = 5; else c.followDistance = 1;
	 * c.outStream.writeWord(c.followDistance); }
	 */

	 public void followPlayer() {
         if (PlayerHandler.players[c.followId] == null
                         || PlayerHandler.players[c.followId].isDead) {
                 resetFollow();
                 return;
         }
         if (c.freezeTimer > 0) {
                 return;
         }
         if (c.isDead || c.playerLevel[3] <= 0)
                 return;

         int otherX = PlayerHandler.players[c.followId].getX();
         int otherY = PlayerHandler.players[c.followId].getY();

         boolean sameSpot = (c.absX == otherX && c.absY == otherY);

         boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(),
                         c.getY(), 2);

         boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(),
                         c.getY(), 4);
         boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(),
                         c.getY(), 6);
         boolean mageDistance = c.goodDistance(otherX, otherY, c.getX(),
                         c.getY(), 7);

         boolean castingMagic = (c.usingMagic || c.mageFollow || c.autocasting || c.spellId > 0)
                         && mageDistance;
         boolean playerRanging = (c.usingRangeWeapon)
                         && rangeWeaponDistance;
         boolean playerBowOrCross = (c.usingBow) && bowDistance;

         if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
                 c.followId = 0;
                 resetFollow();
                 return;
         }
         c.faceUpdate(c.followId + 32768);
         if (!sameSpot) {
                 if (c.playerIndex > 0 && !c.usingSpecial && c.inWild()) {
                         if (c.usingSpecial && (playerRanging || playerBowOrCross)) {
                                 c.stopMovement();
                                 return;
                         }
                         if (castingMagic || playerRanging || playerBowOrCross) {
                                 c.stopMovement();
                                 return;
                         }
                         if (c.getCombat().usingHally() && hallyDistance) {
                                 c.stopMovement();
                                 return;
                         }
                 }
         }
         if (otherX == c.absX && otherY == c.absY) {
                 int r = Misc.random(3);
                 switch (r) {
                 case 0:
                         walkTo(0, -1);
                         break;
                 case 1:
                         walkTo(0, 1);
                         break;
                 case 2:
                         walkTo(1, 0);
                         break;
                 case 3:
                         walkTo(-1, 0);
                         break;
                 }
         } else if (c.isRunning2) {
                 if (otherY > c.getY() && otherX == c.getX()) {
                         playerWalk(otherX, otherY - 1);
                 } else if (otherY < c.getY() && otherX == c.getX()) {
                         playerWalk(otherX, otherY + 1);
                 } else if (otherX > c.getX() && otherY == c.getY()) {
                         playerWalk(otherX - 1, otherY);
                 } else if (otherX < c.getX() && otherY == c.getY()) {
                         playerWalk(otherX + 1, otherY);
                 } else if (otherX < c.getX() && otherY < c.getY()) {
                         playerWalk(otherX + 1, otherY + 1);
                 } else if (otherX > c.getX() && otherY > c.getY()) {
                         playerWalk(otherX - 1, otherY - 1);
                 } else if (otherX < c.getX() && otherY > c.getY()) {
                         playerWalk(otherX + 1, otherY - 1);
                 } else if (otherX > c.getX() && otherY < c.getY()) {
                         playerWalk(otherX + 1, otherY - 1);
                 }
         } else {
                 if (otherY > c.getY() && otherX == c.getX()) {
                         playerWalk(otherX, otherY - 1);
                 } else if (otherY < c.getY() && otherX == c.getX()) {
                         playerWalk(otherX, otherY + 1);
                 } else if (otherX > c.getX() && otherY == c.getY()) {
                         playerWalk(otherX - 1, otherY);
                 } else if (otherX < c.getX() && otherY == c.getY()) {
                         playerWalk(otherX + 1, otherY);
                 } else if (otherX < c.getX() && otherY < c.getY()) {
                         playerWalk(otherX + 1, otherY + 1);
                 } else if (otherX > c.getX() && otherY > c.getY()) {
                         playerWalk(otherX - 1, otherY - 1);
                 } else if (otherX < c.getX() && otherY > c.getY()) {
                         playerWalk(otherX + 1, otherY - 1);
                 } else if (otherX > c.getX() && otherY < c.getY()) {
                         playerWalk(otherX - 1, otherY + 1);
                 }
         }
         c.faceUpdate(c.followId+32768);
 }
	 
	 public void followNpc() {
			if(Server.npcHandler.npcs[c.followId2] == null || Server.npcHandler.npcs[c.followId2].isDead) {
				c.followId2 = 0;
				return;
			}		
			if(c.freezeTimer > 0) {
				return;
			}
			if (c.isDead || c.playerLevel[3] <= 0)
				return;
			
			int otherX = Server.npcHandler.npcs[c.followId2].getX();
			int otherY = Server.npcHandler.npcs[c.followId2].getY();
			boolean withinDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
			boolean goodDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1);
			boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
			boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 8);
			boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 4);
			boolean sameSpot = c.absX == otherX && c.absY == otherY;
			if(!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
				c.followId2 = 0;
				return;
			}
			if(c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1)) {
				if (otherX != c.getX() && otherY != c.getY()) {
					stopDiagonal(otherX, otherY);
					return;
				}
			}
			
			if((c.usingBow || c.mageFollow || (c.npcIndex > 0 && c.autocastId > 0)) && bowDistance && !sameSpot) {
				return;
			}

			if(c.getCombat().usingHally() && hallyDistance && !sameSpot) {
				return;
			}

			if(c.usingRangeWeapon && rangeWeaponDistance && !sameSpot) {
				return;
			}
			
			c.faceUpdate(c.followId2);
			if (otherX == c.absX && otherY == c.absY) {
				int r = Misc.random(3);
				switch (r) {
					case 0:
						walkTo(0,-1);
					break;
					case 1:
						walkTo(0,1);
					break;
					case 2:
						walkTo(1,0);
					break;
					case 3:
						walkTo(-1,0);
					break;			
				}		
			} else if(c.isRunning2 && !withinDistance) {
				/*if(otherY > c.getY() && otherX == c.getX()) {
					walkTo(0, getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
				} else if(otherY < c.getY() && otherX == c.getX()) {
					walkTo(0, getMove(c.getY(), otherY + 1) + getMove(c.getY(), otherY + 1));
				} else if(otherX > c.getX() && otherY == c.getY()) {
					walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(), otherX - 1), 0);
				} else if(otherX < c.getX() && otherY == c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), 0);
				} else if(otherX < c.getX() && otherY < c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY + 1) + getMove(c.getY(), otherY + 1));
				} else if(otherX > c.getX() && otherY > c.getY()) {
					walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(), otherX - 1), getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
				} else if(otherX < c.getX() && otherY > c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
				} else if(otherX > c.getX() && otherY < c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
				} 
			} else {
				if(otherY > c.getY() && otherX == c.getX()) {
					walkTo(0, getMove(c.getY(), otherY - 1));
				} else if(otherY < c.getY() && otherX == c.getX()) {
					walkTo(0, getMove(c.getY(), otherY + 1));
				} else if(otherX > c.getX() && otherY == c.getY()) {
					walkTo(getMove(c.getX(), otherX - 1), 0);
				} else if(otherX < c.getX() && otherY == c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1), 0);
				} else if(otherX < c.getX() && otherY < c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY + 1));
				} else if(otherX > c.getX() && otherY > c.getY()) {
					walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(), otherY - 1));
				} else if(otherX < c.getX() && otherY > c.getY()) {
					walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY - 1));
				} else if(otherX > c.getX() && otherY < c.getY()) {
					walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(), otherY + 1));
				}*/
				if(otherY > c.getY() && otherX == c.getX()) {
					//walkTo(0, getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
					playerWalk(otherX, otherY - 1);
				} else if(otherY < c.getY() && otherX == c.getX()) {
					//walkTo(0, getMove(c.getY(), otherY + 1) + getMove(c.getY(), otherY + 1));
					playerWalk(otherX, otherY + 1);
				} else if(otherX > c.getX() && otherY == c.getY()) {
					//walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(), otherX - 1), 0);
					playerWalk(otherX - 1, otherY);
				} else if(otherX < c.getX() && otherY == c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), 0);
					playerWalk(otherX + 1, otherY);
				} else if(otherX < c.getX() && otherY < c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY + 1) + getMove(c.getY(), otherY + 1));
					playerWalk(otherX + 1, otherY + 1);
				} else if(otherX > c.getX() && otherY > c.getY()) {
					//walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(), otherX - 1), getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
					playerWalk(otherX - 1, otherY - 1);
				} else if(otherX < c.getX() && otherY > c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
					playerWalk(otherX + 1, otherY - 1);
				} else if(otherX > c.getX() && otherY < c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY - 1) + getMove(c.getY(), otherY - 1));
					playerWalk(otherX + 1, otherY - 1);
				}
			} else {
				if(otherY > c.getY() && otherX == c.getX()) {
					//walkTo(0, getMove(c.getY(), otherY - 1));
					playerWalk(otherX, otherY - 1);
				} else if(otherY < c.getY() && otherX == c.getX()) {
					//walkTo(0, getMove(c.getY(), otherY + 1));
					playerWalk(otherX, otherY + 1);
				} else if(otherX > c.getX() && otherY == c.getY()) {
					//walkTo(getMove(c.getX(), otherX - 1), 0);
					playerWalk(otherX - 1, otherY);
				} else if(otherX < c.getX() && otherY == c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1), 0);
					playerWalk(otherX + 1, otherY);
				} else if(otherX < c.getX() && otherY < c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY + 1));
					playerWalk(otherX + 1, otherY + 1);
				} else if(otherX > c.getX() && otherY > c.getY()) {
					//walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(), otherY - 1));
					playerWalk(otherX - 1, otherY - 1);
				} else if(otherX < c.getX() && otherY > c.getY()) {
					//walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(), otherY - 1));
					playerWalk(otherX + 1, otherY - 1);
				} else if(otherX > c.getX() && otherY < c.getY()) {
					//walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(), otherY + 1));
					playerWalk(otherX - 1, otherY + 1);
				}
			}
			c.faceUpdate(c.followId2);
		}

	public int getRunningMove(int i, int j) {
		if (j - i > 2)
			return 2;
		else if (j - i < -2)
			return -2;
		else
			return j - i;
	}

	public void resetFollow() {
		c.followId = 0;
		c.followId2 = 0;
		c.mageFollow = false;
		c.outStream.createFrame(174);
		c.outStream.writeWord(0);
		c.outStream.writeByte(0);
		c.outStream.writeWord(1);
	}

	public void walkTo(int i, int j) {
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public void walkTo2(int i, int j) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 0;
        if(++c.newWalkCmdSteps > 50)
            c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
        int l = c.getY() + j;
        l -= c.mapRegionY * 8;

        for(int n = 0; n < c.newWalkCmdSteps; n++) {
            c.getNewWalkCmdX()[n] += k;
            c.getNewWalkCmdY()[n] += l;
        }
    }
	
	public void stopDiagonal(int otherX, int otherY) {
		if (c.freezeDelay > 0)
			return;
if (c.freezeTimer > 0) //player can't move
      return;			
		c.newWalkCmdSteps = 1;
		int xMove = otherX - c.getX();
		int yMove = 0;
		if (xMove == 0)
			yMove = otherY - c.getY();
		/*if (!clipHor) {
			yMove = 0;
		} else if (!clipVer) {
			xMove = 0;	
		}*/
		
		int k = c.getX() + xMove;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
        int l = c.getY() + yMove;
        l -= c.mapRegionY * 8;
		
		for(int n = 0; n < c.newWalkCmdSteps; n++) {
            c.getNewWalkCmdX()[n] += k;
            c.getNewWalkCmdY()[n] += l;
        }
		
	}

	public void walkToCheck(int i, int j) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 0;
        if(++c.newWalkCmdSteps > 50)
            c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
        k -= c.mapRegionX * 8;
        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
        int l = c.getY() + j;
        l -= c.mapRegionY * 8;
		
		for(int n = 0; n < c.newWalkCmdSteps; n++) {
            c.getNewWalkCmdX()[n] += k;
            c.getNewWalkCmdY()[n] += l;
        }
	}
	
	public int getMove(int place1,int place2) {
		if (System.currentTimeMillis() - c.lastSpear < 4000)
			return 0;
		if ((place1 - place2) == 0) {
            return 0;
		} else if ((place1 - place2) < 0) {
			return 1;
		} else if ((place1 - place2) > 0) {
			return -1;
		}
        return 0;
   	}

	public boolean fullVeracs() {
		return c.playerEquipment[c.playerHat] == 4753
				&& c.playerEquipment[c.playerChest] == 4757
				&& c.playerEquipment[c.playerLegs] == 4759
				&& c.playerEquipment[c.playerWeapon] == 4755;
	}

	public boolean fullGuthans() {
		return c.playerEquipment[c.playerHat] == 4724
				&& c.playerEquipment[c.playerChest] == 4728
				&& c.playerEquipment[c.playerLegs] == 4730
				&& c.playerEquipment[c.playerWeapon] == 4726;
	}

	/**
	 * reseting animation
	 **/
	public void resetAnimation() {
		c.getCombat().getPlayerAnimIndex(
				c.getItems().getItemName(c.playerEquipment[c.playerWeapon])
						.toLowerCase());
		c.startAnimation(c.playerStandIndex);
		requestUpdates();
	}

	public void requestUpdates() {
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}

	public void handleAlt(int id) {
		if (!c.getItems().playerHasItem(id)) {
			c.getItems().addItem(id, 1);
		}
	}

	public void levelUp(int skill) {
		int totalLevel = (getLevelForXP(c.playerXP[0])
				+ getLevelForXP(c.playerXP[1]) + getLevelForXP(c.playerXP[2])
				+ getLevelForXP(c.playerXP[3]) + getLevelForXP(c.playerXP[4])
				+ getLevelForXP(c.playerXP[5]) + getLevelForXP(c.playerXP[6])
				+ getLevelForXP(c.playerXP[7]) + getLevelForXP(c.playerXP[8])
				+ getLevelForXP(c.playerXP[9]) + getLevelForXP(c.playerXP[10])
				+ getLevelForXP(c.playerXP[11]) + getLevelForXP(c.playerXP[12])
				+ getLevelForXP(c.playerXP[13]) + getLevelForXP(c.playerXP[14])
				+ getLevelForXP(c.playerXP[15]) + getLevelForXP(c.playerXP[16])
				+ getLevelForXP(c.playerXP[17]) + getLevelForXP(c.playerXP[18])
				+ getLevelForXP(c.playerXP[19]) + getLevelForXP(c.playerXP[20]));
		sendFrame126("Total Lvl: " + totalLevel, 3984);
		switch (skill) {
		case 0:
			sendFrame126("Congratulations, you just advanced an attack level!",
					6248);
			sendFrame126("Your attack level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6249);
			c.sendMessage("Congratulations, you just advanced an attack level.");
			sendFrame164(6247);
			break;

		case 1:
			sendFrame126("Congratulations, you just advanced a defence level!",
					6254);
			sendFrame126("Your defence level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6255);
			c.sendMessage("Congratulations, you just advanced a defence level.");
			sendFrame164(6253);
			break;

		case 2:
			sendFrame126(
					"Congratulations, you just advanced a strength level!",
					6207);
			sendFrame126("Your strength level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6208);
			c.sendMessage("Congratulations, you just advanced a strength level.");
			sendFrame164(6206);
			break;

		case 3:
			sendFrame126(
					"Congratulations, you just advanced a hitpoints level!",
					6217);
			sendFrame126("Your hitpoints level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6218);
			c.sendMessage("Congratulations, you just advanced a hitpoints level.");
			sendFrame164(6216);
			break;

		case 4:
			sendFrame126("Congratulations, you just advanced a ranged level!",
					5453);
			sendFrame126("Your ranged level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6114);
			c.sendMessage("Congratulations, you just advanced a ranging level.");
			sendFrame164(4443);
			break;

		case 5:
			sendFrame126("Congratulations, you just advanced a prayer level!",
					6243);
			sendFrame126("Your prayer level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6244);
			c.sendMessage("Congratulations, you just advanced a prayer level.");
			sendFrame164(6242);
			break;

		case 6:
			sendFrame126("Congratulations, you just advanced a magic level!",
					6212);
			sendFrame126("Your magic level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6213);
			c.sendMessage("Congratulations, you just advanced a magic level.");
			sendFrame164(6211);
			break;

		case 7:
			sendFrame126("Congratulations, you just advanced a cooking level!",
					6227);
			sendFrame126("Your cooking level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6228);
			c.sendMessage("Congratulations, you just advanced a cooking level.");
			sendFrame164(6226);
			break;

		case 8:
			sendFrame126(
					"Congratulations, you just advanced a woodcutting level!",
					4273);
			sendFrame126("Your woodcutting level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4274);
			c.sendMessage("Congratulations, you just advanced a woodcutting level.");
			sendFrame164(4272);
			break;

		case 9:
			sendFrame126(
					"Congratulations, you just advanced a fletching level!",
					6232);
			sendFrame126("Your fletching level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6233);
			c.sendMessage("Congratulations, you just advanced a fletching level.");
			sendFrame164(6231);
			break;

		case 10:
			sendFrame126("Congratulations, you just advanced a fishing level!",
					6259);
			sendFrame126("Your fishing level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6260);
			c.sendMessage("Congratulations, you just advanced a fishing level.");
			sendFrame164(6258);
			break;

		case 11:
			sendFrame126(
					"Congratulations, you just advanced a fire making level!",
					4283);
			sendFrame126("Your firemaking level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4284);
			c.sendMessage("Congratulations, you just advanced a fire making level.");
			sendFrame164(4282);
			break;

		case 12:
			sendFrame126(
					"Congratulations, you just advanced a crafting level!",
					6264);
			sendFrame126("Your crafting level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6265);
			c.sendMessage("Congratulations, you just advanced a crafting level.");
			sendFrame164(6263);
			break;

		case 13:
			sendFrame126(
					"Congratulations, you just advanced a smithing level!",
					6222);
			sendFrame126("Your smithing level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6223);
			c.sendMessage("Congratulations, you just advanced a smithing level.");
			sendFrame164(6221);
			break;

		case 14:
			sendFrame126("Congratulations, you just advanced a mining level!",
					4417);
			sendFrame126("Your mining level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4438);
			c.sendMessage("Congratulations, you just advanced a mining level.");
			sendFrame164(4416);
			break;

		case 15:
			sendFrame126(
					"Congratulations, you just advanced a herblore level!",
					6238);
			sendFrame126("Your herblore level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6239);
			c.sendMessage("Congratulations, you just advanced a herblore level.");
			sendFrame164(6237);
			break;

		case 16:
			sendFrame126("Congratulations, you just advanced a agility level!",
					4278);
			sendFrame126("Your agility level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4279);
			c.sendMessage("Congratulations, you just advanced an agility level.");
			sendFrame164(4277);
			break;

		case 17:
			sendFrame126(
					"Congratulations, you just advanced a thieving level!",
					4263);
			sendFrame126("Your theiving level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4264);
			c.sendMessage("Congratulations, you just advanced a thieving level.");
			sendFrame164(4261);
			break;

		case 18:
			sendFrame126("Congratulations, you just advanced a slayer level!",
					12123);
			sendFrame126("Your slayer level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 12124);
			c.sendMessage("Congratulations, you just advanced a slayer level.");
			sendFrame164(12122);
			break;

		case 20:
			sendFrame126(
					"Congratulations, you just advanced a runecrafting level!",
					4268);
			sendFrame126("Your runecrafting level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4269);
			c.sendMessage("Congratulations, you just advanced a runecrafting level.");
			sendFrame164(4267);
			break;
		}
		c.dialogueAction = 0;
		c.nextChat = 0;
	}
	
	public void resetLevels(){
		c.playerXP[0] = getXPForLevel(1)+5;
		c.playerXP[1] = getXPForLevel(1)+5;
		c.playerXP[2] = getXPForLevel(1)+5;
		c.playerXP[3] = getXPForLevel(10)+5;
		c.playerXP[4] = getXPForLevel(1)+5;
		c.playerXP[5] = getXPForLevel(1)+5;
		c.playerXP[6] = getXPForLevel(1)+5;
		c.playerLevel[0] = getLevelForXP(c.playerXP[0]);
		c.playerLevel[1] = getLevelForXP(c.playerXP[1]);
		c.playerLevel[2] = getLevelForXP(c.playerXP[2]);
		c.playerLevel[3] = getLevelForXP(c.playerXP[3]);
		c.playerLevel[4] = getLevelForXP(c.playerXP[4]);
		c.playerLevel[5] = getLevelForXP(c.playerXP[5]);
		c.playerLevel[6] = getLevelForXP(c.playerXP[6]);
		refreshSkill(0);	
		refreshSkill(1);	
		refreshSkill(2);	
		refreshSkill(3);	
		refreshSkill(4);	
		refreshSkill(5);	
		refreshSkill(6);	
		return;
}

	public void refreshSkill(int i) {
		switch (i) {
		case 0:
			sendFrame126("" + c.playerLevel[0] + "", 4004);
			sendFrame126("" + getLevelForXP(c.playerXP[0]) + "", 4005);
			sendFrame126("" + c.playerXP[0] + "", 4044);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[0]) + 1)
					+ "", 4045);
			break;

		case 1:
			sendFrame126("" + c.playerLevel[1] + "", 4008);
			sendFrame126("" + getLevelForXP(c.playerXP[1]) + "", 4009);
			sendFrame126("" + c.playerXP[1] + "", 4056);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[1]) + 1)
					+ "", 4057);
			break;

		case 2:
			sendFrame126("" + c.playerLevel[2] + "", 4006);
			sendFrame126("" + getLevelForXP(c.playerXP[2]) + "", 4007);
			sendFrame126("" + c.playerXP[2] + "", 4050);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[2]) + 1)
					+ "", 4051);
			break;

		case 3:
			sendFrame126("" + c.playerLevel[3] + "", 4016);
			sendFrame126("" + getLevelForXP(c.playerXP[3]) + "", 4017);
			sendFrame126("" + c.playerXP[3] + "", 4080);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[3]) + 1)
					+ "", 4081);
			break;

		case 4:
			sendFrame126("" + c.playerLevel[4] + "", 4010);
			sendFrame126("" + getLevelForXP(c.playerXP[4]) + "", 4011);
			sendFrame126("" + c.playerXP[4] + "", 4062);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[4]) + 1)
					+ "", 4063);
			break;

		case 5:
			sendFrame126("" + c.playerLevel[5] + "", 4012);
			sendFrame126("" + getLevelForXP(c.playerXP[5]) + "", 4013);
			sendFrame126("" + c.playerXP[5] + "", 4068);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[5]) + 1)
					+ "", 4069);
			sendFrame126("" + c.playerLevel[5] + "/"
					+ getLevelForXP(c.playerXP[5]) + "", 687);// Prayer frame
			break;

		case 6:
			sendFrame126("" + c.playerLevel[6] + "", 4014);
			sendFrame126("" + getLevelForXP(c.playerXP[6]) + "", 4015);
			sendFrame126("" + c.playerXP[6] + "", 4074);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[6]) + 1)
					+ "", 4075);
			break;

		case 7:
			sendFrame126("" + c.playerLevel[7] + "", 4034);
			sendFrame126("" + getLevelForXP(c.playerXP[7]) + "", 4035);
			sendFrame126("" + c.playerXP[7] + "", 4134);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[7]) + 1)
					+ "", 4135);
			break;

		case 8:
			sendFrame126("" + c.playerLevel[8] + "", 4038);
			sendFrame126("" + getLevelForXP(c.playerXP[8]) + "", 4039);
			sendFrame126("" + c.playerXP[8] + "", 4146);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[8]) + 1)
					+ "", 4147);
			break;

		case 9:
			sendFrame126("" + c.playerLevel[9] + "", 4026);
			sendFrame126("" + getLevelForXP(c.playerXP[9]) + "", 4027);
			sendFrame126("" + c.playerXP[9] + "", 4110);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[9]) + 1)
					+ "", 4111);
			break;

		case 10:
			sendFrame126("" + c.playerLevel[10] + "", 4032);
			sendFrame126("" + getLevelForXP(c.playerXP[10]) + "", 4033);
			sendFrame126("" + c.playerXP[10] + "", 4128);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[10]) + 1)
					+ "", 4129);
			break;

		case 11:
			sendFrame126("" + c.playerLevel[11] + "", 4036);
			sendFrame126("" + getLevelForXP(c.playerXP[11]) + "", 4037);
			sendFrame126("" + c.playerXP[11] + "", 4140);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[11]) + 1)
					+ "", 4141);
			break;

		case 12:
			sendFrame126("" + c.playerLevel[12] + "", 4024);
			sendFrame126("" + getLevelForXP(c.playerXP[12]) + "", 4025);
			sendFrame126("" + c.playerXP[12] + "", 4104);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[12]) + 1)
					+ "", 4105);
			break;

		case 13:
			sendFrame126("" + c.playerLevel[13] + "", 4030);
			sendFrame126("" + getLevelForXP(c.playerXP[13]) + "", 4031);
			sendFrame126("" + c.playerXP[13] + "", 4122);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[13]) + 1)
					+ "", 4123);
			break;

		case 14:
			sendFrame126("" + c.playerLevel[14] + "", 4028);
			sendFrame126("" + getLevelForXP(c.playerXP[14]) + "", 4029);
			sendFrame126("" + c.playerXP[14] + "", 4116);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[14]) + 1)
					+ "", 4117);
			break;

		case 15:
			sendFrame126("" + c.playerLevel[15] + "", 4020);
			sendFrame126("" + getLevelForXP(c.playerXP[15]) + "", 4021);
			sendFrame126("" + c.playerXP[15] + "", 4092);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[15]) + 1)
					+ "", 4093);
			break;

		case 16:
			sendFrame126("" + c.playerLevel[16] + "", 4018);
			sendFrame126("" + getLevelForXP(c.playerXP[16]) + "", 4019);
			sendFrame126("" + c.playerXP[16] + "", 4086);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[16]) + 1)
					+ "", 4087);
			break;

		case 17:
			sendFrame126("" + c.playerLevel[17] + "", 4022);
			sendFrame126("" + getLevelForXP(c.playerXP[17]) + "", 4023);
			sendFrame126("" + c.playerXP[17] + "", 4098);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[17]) + 1)
					+ "", 4099);
			break;

		case 18:
			sendFrame126("" + c.playerLevel[18] + "", 12166);
			sendFrame126("" + getLevelForXP(c.playerXP[18]) + "", 12167);
			sendFrame126("" + c.playerXP[18] + "", 12171);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[18]) + 1)
					+ "", 12172);
			break;

		case 19:
			sendFrame126("" + c.playerLevel[19] + "", 13926);
			sendFrame126("" + getLevelForXP(c.playerXP[19]) + "", 13927);
			sendFrame126("" + c.playerXP[19] + "", 13921);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[19]) + 1)
					+ "", 13922);
			break;

		case 20:
			sendFrame126("" + c.playerLevel[20] + "", 4152);
			sendFrame126("" + getLevelForXP(c.playerXP[20]) + "", 4153);
			sendFrame126("" + c.playerXP[20] + "", 4157);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[20]) + 1)
					+ "", 4158);
			break;
		}
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor((double) lvl + 300.0
					* Math.pow(2.0, (double) lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXP(int exp) {
		int points = 0;
		int output = 0;
		if (exp > 13034430)
			return 99;
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor((double) lvl + 300.0
					* Math.pow(2.0, (double) lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp) {
				return lvl;
			}
		}
		return 0;
	}

	public boolean addSkillXP(int amount, int skill) {
		if (amount + c.playerXP[skill] < 0 || c.playerXP[skill] > 200000000) {
			if (c.playerXP[skill] > 200000000) {
				c.playerXP[skill] = 200000000;
			}
			return false;
		}
		amount *= Config.SERVER_EXP_BONUS;
		int oldLevel = getLevelForXP(c.playerXP[skill]);
		c.playerXP[skill] += amount;
		if (oldLevel < getLevelForXP(c.playerXP[skill])) {
			if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])
					&& skill != 3 && skill != 5)
				c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
			levelUp(skill);
			c.gfx100(199);
			requestUpdates();
		}
		setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
		refreshSkill(skill);
		return true;
	}

	public void resetBarrows() {
		c.barrowsNpcs[0][1] = 0;
		c.barrowsNpcs[1][1] = 0;
		c.barrowsNpcs[2][1] = 0;
		c.barrowsNpcs[3][1] = 0;
		c.barrowsNpcs[4][1] = 0;
		c.barrowsNpcs[5][1] = 0;
		c.barrowsKillCount = 0;
		c.randomCoffin = Misc.random(3) + 1;
	}

	public static int Barrows[] = { 4708, 4710, 4712, 4714, 4716, 4718, 4720,
			4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747,
			4749, 4751, 4753, 4755, 4757, 4759 };
	public static int Runes[] = { 4740, 558, 560, 565 };
	public static int Pots[] = {};

	public int randomBarrows() {
		return Barrows[(int) (Math.random() * Barrows.length)];
	}

	public int randomRunes() {
		return Runes[(int) (Math.random() * Runes.length)];
	}

	public int randomPots() {
		return Pots[(int) (Math.random() * Pots.length)];
	}

	/**
	 * Show an arrow icon on the selected player.
	 * 
	 * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
	 * @Param j - The player/Npc that the arrow will be displayed above.
	 * @Param k - Keep this set as 0
	 * @Param l - Keep this set as 0
	 */
	public void drawHeadicon(int i, int j, int k, int l) {
		c.outStream.createFrame(254);
		c.outStream.writeByte(i);

		if (i == 1 || i == 10) {
			c.outStream.writeWord(j);
			c.outStream.writeWord(k);
			c.outStream.writeByte(l);
		} else {
			c.outStream.writeWord(k);
			c.outStream.writeWord(l);
			c.outStream.writeByte(j);
		}
	}

	public int getNpcId(int id) {
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] != null) {
				if (NPCHandler.npcs[i].npcId == id) {
					return i;
				}
			}
		}
		return -1;
	}

	public void removeObject(int x, int y) {
		object(-1, x, x, 10, 10);
	}

	private void objectToRemove(int X, int Y) {
		object(-1, X, Y, 10, 10);
	}

	private void objectToRemove2(int X, int Y) {
		object(-1, X, Y, -1, 0);
	}

	public void removeObjects() {
		objectToRemove(2638, 4688);
		objectToRemove2(2635, 4693);
		objectToRemove2(2634, 4693);
	}

	public void handleGlory(int gloryId) {
		c.getDH().sendOption4("Edgeville", "Al Kharid", "Karamja", "Mage Bank");
		c.usingGlory = true;
	}

	public void resetVariables() {
		//c.getFishing().resetFishing();
		c.getCrafting().resetCrafting();
		c.usingGlory = false;
		c.smeltInterface = false;
		c.smeltType = 0;
		c.smeltAmount = 0;
		c.woodcut[0] = c.woodcut[1] = c.woodcut[2] = 0;
		c.mining[0] = c.mining[1] = c.mining[2] = 0;
	}

	public boolean inPitsWait() {
		return c.getX() <= 2404 && c.getX() >= 2394 && c.getY() <= 5175
				&& c.getY() >= 5169;
	}

	public void castleWarsObjects() {
		object(-1, 2373, 3119, -3, 10);
		object(-1, 2372, 3119, -3, 10);
	}

	public int antiFire() {
		int toReturn = 0;
		if (c.antiFirePot)
			toReturn++;
		if (c.playerEquipment[c.playerShield] == 1540 || c.prayerActive[12]
				|| c.playerEquipment[c.playerShield] == 11284)
			toReturn++;
		return toReturn;
	}

	public boolean checkForFlags() {
		int[][] itemsToCheck = { { 995, 100000000 }, { 35, 5 }, { 667, 5 },
				{ 2402, 5 }, { 746, 5 }, { 4151, 150 }, { 565, 100000 },
				{ 560, 100000 }, { 555, 300000 }, { 11235, 10 } };
		for (int j = 0; j < itemsToCheck.length; j++) {
			if (itemsToCheck[j][1] < c.getItems().getTotalCount(
					itemsToCheck[j][0]))
				return true;
		}
		return false;
	}

	public void addStarter() {
					c.getItems().addItem(9703, 1);
					c.getItems().addItem(9704, 1);
					c.getItems().addItem(9705, 1);
					c.getItems().addItem(9706, 25);
					c.getItems().addItem(556, 30);
					c.getItems().addItem(558, 30);
					c.getItems().addItem(1265, 1);
					c.getItems().addItem(1351, 1);
					c.getItems().addItem(303, 1);
					c.getItems().addItem(995, 1500);
					c.getPA().sendStatement("You have received a starter set.");
					removeAllWindows();
	}

	public int getWearingAmount() {
		int count = 0;
		for (int j = 0; j < c.playerEquipment.length; j++) {
			if (c.playerEquipment[j] > 0)
				count++;
		}
		return count;
	}

	public void useOperate(int itemId) {
		switch (itemId) {
		case 1712:
		case 1710:
		case 1708:
		case 1706:
			handleGlory(itemId);
			break;
		case 11283:
		case 11284:
			if (c.playerIndex > 0) {
				c.getCombat().handleDfs(c);
			} else if (c.npcIndex > 0) {
				c.getCombat().handleDfsNPC(c);
			}
			break;
		}
	}

	public void getSpeared(int otherX, int otherY) {
		int x = c.absX - otherX;
		int y = c.absY - otherY;
		if (x > 0)
			x = 1;
		else if (x < 0)
			x = -1;
		if (y > 0)
			y = 1;
		else if (y < 0)
			y = -1;
		moveCheck(x, y);
		c.lastSpear = System.currentTimeMillis();
	}

	public void moveCheck(int xMove, int yMove) {
		movePlayer(c.absX + xMove, c.absY + yMove, c.heightLevel);
	}

	public int findKiller() {
		int killer = c.playerId;
		int damage = 0;
		for (int j = 0; j < Config.MAX_PLAYERS; j++) {
			if (PlayerHandler.players[j] == null)
				continue;
			if (j == c.playerId)
				continue;
			if (c.goodDistance(c.absX, c.absY, PlayerHandler.players[j].absX,
					PlayerHandler.players[j].absY, 40)
					|| c.goodDistance(c.absX, c.absY + 9400,
							PlayerHandler.players[j].absX,
							PlayerHandler.players[j].absY, 40)
					|| c.goodDistance(c.absX, c.absY,
							PlayerHandler.players[j].absX,
							PlayerHandler.players[j].absY + 9400, 40))
				if (c.damageTaken[j] > damage) {
					damage = c.damageTaken[j];
					killer = j;
				}
		}
		return killer;
	}

	public void resetTzhaar() {
		c.waveId = -1;
		c.tzhaarToKill = -1;
		c.tzhaarKilled = -1;
		c.getPA().movePlayer(2438, 5168, 0);
	}

	public void enterCaves() {
		c.getPA().movePlayer(2413, 5117, c.playerId * 4);
		c.waveId = 0;
		c.tzhaarToKill = -1;
		c.tzhaarKilled = -1;
		Server.fightCaves.spawnNextWave(c);
	}

	public void appendPoison(int damage) {
		if (System.currentTimeMillis() - c.lastPoisonSip > c.poisonImmune) {
			c.sendMessage("You have been poisoned.");
			c.poisonDamage = damage;
		}
	}

	public boolean checkForPlayer(int x, int y) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				if (p.getX() == x && p.getY() == y)
					return true;
			}
		}
		return false;
	}

	public void checkPouch(int i) {
		if (i < 0)
			return;
		c.sendMessage("This pouch has " + c.pouches[i] + " rune ess in it.");
	}

	public void fillPouch(int i) {
		if (i < 0)
			return;
		int toAdd = c.POUCH_SIZE[i] - c.pouches[i];
		if (toAdd > c.getItems().getItemAmount(1436)) {
			toAdd = c.getItems().getItemAmount(1436);
		}
		if (toAdd > c.POUCH_SIZE[i] - c.pouches[i])
			toAdd = c.POUCH_SIZE[i] - c.pouches[i];
		if (toAdd > 0) {
			c.getItems().deleteItem(1436, toAdd);
			c.pouches[i] += toAdd;
		}
	}

	public void emptyPouch(int i) {
		if (i < 0)
			return;
		int toAdd = c.pouches[i];
		if (toAdd > c.getItems().freeSlots()) {
			toAdd = c.getItems().freeSlots();
		}
		if (toAdd > 0) {
			c.getItems().addItem(1436, toAdd);
			c.pouches[i] -= toAdd;
		}
	}
	
	public static int ClueLvl1[] = {
		2583,2585,2587,2589,2591,2593,2595,2597,3472,3472,
		2631,2633,2635,2637,7362,7364,7366,7368,7332,7338,
		7344,7350,7356,7386,7388,7490,7392,7394,7396,10404,10366,
		10406,10408,10410,10412,10414,10316,10318,10320,10322,
		10324,2583,2585,2587,2589,2591,2593,2595,2597,3472,3472,
		2631,2633,2635,2637,7362,7364,7366,7368,7332,7338,
		7344,7350,7356,7386,7388,7490,7392,7394,7396,10404,10366,
		10406,10408,10410,10412,10414,10316,10318,10320,10322,10324,
		1438,1440,1438,1440,1442,1442,1444,1444,1446,1458,		//talisman
		1446,1448,1448,1450,1450,1452,1452,1454,1454,1456,1456,1458,//talisman
		1694,1696,1698,1700,1702,1694,1696,1698,1700,1702,			//Amulets
		1694,1696,1698,1700,1702,1694,1696,1698,1700,1702,			//Amulets
		1077,1077,1077,1077,1077,1077,1077,1077,1077,1077,			//black legs
		1089,1089,1089,1089,1089,1089,1089,1089,1089,1089,			//black skirt
		1125,1125,1125,1125,1125,1125,1125,1125,1125,1125,			//black plate
		1195,1195,1195,1195,1195,1195,1195,1195,1195,1195,			//black kite
		1297,1297,1297,1297,1297,1297,1297,1297,1297,1297,			//black long
		1297,1297,1297,1297,1297,1297,1297,1297,1297,1297,			//black sword
		1313,1313,1313,1313,1313,1313,1313,1313,1313,1313,			//black2h
		1327,1327,1327,1327,1327,1327,1327,1327,1327,1327,			//black scim
		};
		public int randomClueLvl1() {
			return ClueLvl1[(int)(Math.random()*ClueLvl1.length)];
		}
	
	public static int ClueLvl2[] = {
		2599,2601,2603,2605,2607,2609,2611,2613,2577,2579,
		2645,2647,2649,7334,7340,7346,7352,7358,10400,10402,
		10416,10418,10420,10422,10436,10438,10364,10446,10448,
		10446,10448,10450,10452,10454,10456,10458,10460,10462,
		10464,10466,10468,2599,2601,2603,2605,2607,2609,2611,
		2613,2577,2579,2645,2647,2649,7334,7340,7346,7352,7358,
		10400,10402,10416,10418,10420,10422,10436,10438,10364,
		10446,10448,10446,10448,10450,10452,10454,10456,10458,
		10460,10462,10464,10466,10468,7334,7340,7346,7352,7358,
		1438,1438,1440,1438,1440,1442,1442,1444,1444,1446,1458,		//talisman
		1446,1448,1448,1450,1450,1452,1452,1454,1454,1456,1456,1458,//talisman
		1694,1696,1698,1700,1702,1694,1696,1698,1700,1702,			//Amulets
		1694,1696,1698,1700,1702,1694,1696,1698,1700,1702,			//Amulets
		1073,1073,1073,1073,1073,1073,1073,1073,1073,1073,			//addy legs
		1091,1091,1091,1091,1091,1091,1091,1091,1091,1091,			//addy skirt
		1123,1123,1123,1123,1123,1123,1123,1123,1123,1123,			//addy plate
		1199,1199,1199,1199,1199,1199,1199,1199,1199,1199,			//addy kite
		1301,1301,1301,1301,1301,1301,1301,1301,1301,1301,			//addy long
		1271,1271,1271,1271,1271,1271,1271,1271,1271,1271,			//addy pickaxe
		1317,1317,1317,1317,1317,1317,1317,1317,1317,1317,			//addy 2h
		1331,1331,1331,1331,1331,1331,1331,1331,1331,1331,			//addy scim
		};
		public int randomClueLvl2() {
			return ClueLvl2[(int)(Math.random()*ClueLvl2.length)];
		}

	public static int ClueLvl3[] = {
		7336,7342,7348,7354,7360,7374,7376,7382,7384,7398,7399,7400,
		2614,2617,2619,2621,2623,2625,2627,2629,2651,2653,2655,2657,
		3481,3483,3486,3488,3478,3479,3480,2581,2639,362643,
		2659,2661,2663,2665,2667,2669,2671,2673,2675,3478,10368,
		10370,10372,10374,10376,10378,10380,10382,10384,10386,10388,
		10390,10440,10442,10444,10470,10476,10474,10362,10330,10332,
		10334,10336,10338,10340,10342,10344,10346,10348,10350,10352,
		1079,1079,1079,1079,1079,1079,1079,1079,1079,1079,			//rune legs
		1093,1093,1093,1093,1093,1093,1093,1093,1093,1093,			//rune skirt
		112,1127,1127,1127,1127,1127,1127,1127,1127,1127,			//rune plate
		1201,1201,1201,1201,1201,1201,1201,1201,1201,1201,			//rune kite
		1303,1303,1303,1303,1303,1303,1303,1303,1303,1303,			//rune long
		1273,1273,1273,1273,1273,1273,1273,1273,1273,1273,			//rune pickaxe
		1319,1319,1319,1319,1319,1319,1319,1319,1319,1319,			//rune 2h
		1333,1333,1333,1333,1333,1333,1333,1333,1333,1333,			//rune scim
		
		};
		public int randomClueLvl3() {
			return ClueLvl3[(int)(Math.random()*ClueLvl3.length)];
		}
		
		public static int YanilleChest[] = {2577, 2581, 2571, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592, 592};
				public int randomYanilleChest() {
					return YanilleChest[(int)(Math.random()*YanilleChest.length)];
				}
		
	public void fixAllBarrows() {
		int totalCost = 0;
		int cashAmount = c.getItems().getItemAmount(995);
		for (int j = 0; j < c.playerItems.length; j++) {
			boolean breakOut = false;
			for (int i = 0; i < c.getItems().brokenBarrows.length; i++) {
				if (c.playerItems[j] - 1 == c.getItems().brokenBarrows[i][1]) {
					if (totalCost + 80000 > cashAmount) {
						breakOut = true;
						c.sendMessage("You have run out of money.");
						break;
					} else {
						totalCost += 80000;
					}
					c.playerItems[j] = c.getItems().brokenBarrows[i][0] + 1;
				}
			}
			if (breakOut)
				break;
		}
		if (totalCost > 0)
			c.getItems().deleteItem(995, c.getItems().getItemSlot(995),
					totalCost);
	}

	public void handleLoginText() {
		// Ancient Spells
				c.getPA().sendFrame126("Yanille", 13037);
				c.getPA().sendFrame126("Canfis", 13047);
				c.getPA().sendFrame126("Sophanem", 13055);
				c.getPA().sendFrame126("Its really hot here!", 13056);
				c.getPA().sendFrame126("Rellekka", 13063);
				c.getPA().sendFrame126("Ardougne", 13071);
				c.getPA().sendFrame126("Lletya", 13072); //dareeyak description
				c.getPA().sendFrame126("Lletya.", 13081); //carrallanger
				c.getPA().sendFrame126("Elfs,Faires live here.", 13082); //carralanger description
				c.getPA().sendFrame126("Burthrope", 13089); //annakarl
				c.getPA().sendFrame126("Extreme Slaying!", 13090); //annakarl description
				c.getPA().sendFrame126("????????", 13097); //ghorrock
				c.getPA().sendFrame126("Suggest another area here.", 13098); //ghorrock description
				// Modern Spells
				c.getPA().sendFrame126("Yanille", 1300);
				c.getPA().sendFrame126("Home of the mighty dad and miners.", 1301);
				c.getPA().sendFrame126("Canfis", 1325);
				c.getPA().sendFrame126("Home of the swamp monsters.", 1326);
				c.getPA().sendFrame126("Sophanem", 1350);
				c.getPA().sendFrame126("Its really hot here!", 1351);
				c.getPA().sendFrame126("Rellekka", 1382);
				c.getPA().sendFrame126("Rock creatures like to stroll here.", 1383);
				c.getPA().sendFrame126("Ardougne", 1415);
				c.getPA().sendFrame126("Thiever's Headquarters.", 1416);
				c.getPA().sendFrame126("Lletya", 1454); //watchtower
				c.getPA().sendFrame126("Elfs,Fairies live here.", 1455); //watchtower description
				c.getPA().sendFrame126("Burthrope", 7457); //trollheim
				c.getPA().sendFrame126("Extreme Slaying!", 7458); //trollheim description
				c.getPA().sendFrame126("??????", 18472);	//ape atoll
				c.getPA().sendFrame126("Suggest Another Area Here.", 18473); //ape atoll description
	}

	public void handleWeaponStyle() {
		if (c.fightMode == 0) {
			c.getPA().sendFrame36(43, c.fightMode);
		} else if (c.fightMode == 1) {
			c.getPA().sendFrame36(43, 3);
		} else if (c.fightMode == 2) {
			c.getPA().sendFrame36(43, 1);
		} else if (c.fightMode == 3) {
			c.getPA().sendFrame36(43, 2);
		}
	}

	public void hitDragonClaws(Client c2, int random) {
		// TODO Auto-generated method stub
		
	}

	public void loadQuests() {
		c.getPA().sendFrame126("SSL Quests:", 663);
		c.getPA().sendFrame126("", 13136);
		c.getPA().sendFrame126("", 673);
		if(c.getStart == 0) {
			c.getPA().sendFrame126("@red@Getting Started", 7332);
		} else if(c.getStart == 3) {
			c.getPA().sendFrame126("@gre@Getting Started", 7332);
		} else {
			c.getPA().sendFrame126("@yel@Getting Started", 7332);
		}
		c.getPA().sendFrame126("", 7333);
        c.getPA().sendFrame126("", 7334);
        c.getPA().sendFrame126("", 7336);
        c.getPA().sendFrame126("", 7383);
        c.getPA().sendFrame126("", 7339);
        c.getPA().sendFrame126("", 7338);
        c.getPA().sendFrame126("", 7340);
        c.getPA().sendFrame126("", 7346);
        c.getPA().sendFrame126("", 7341);
        c.getPA().sendFrame126("", 7342);
        c.getPA().sendFrame126("", 7337);
        c.getPA().sendFrame126("", 7343);
        c.getPA().sendFrame126("", 7335);
        c.getPA().sendFrame126("", 7344);
        c.getPA().sendFrame126("", 7345);
        c.getPA().sendFrame126("", 7347);
        c.getPA().sendFrame126("", 7348);
        c.getPA().sendFrame126("", 12772);
        c.getPA().sendFrame126("", 7352);
		c.getPA().sendFrame126("", 12129);
		c.getPA().sendFrame126("", 8438);
		c.getPA().sendFrame126("", 18517);
		c.getPA().sendFrame126("", 15847);
		c.getPA().sendFrame126("", 15487);
		c.getPA().sendFrame126("", 12852);
		c.getPA().sendFrame126("", 7354);
		c.getPA().sendFrame126("", 7355);
		c.getPA().sendFrame126("", 7356);
		c.getPA().sendFrame126("", 8679);
		c.getPA().sendFrame126("", 7459);
		c.getPA().sendFrame126("", 7357);
		c.getPA().sendFrame126("", 14912);
		c.getPA().sendFrame126("", 249);
		c.getPA().sendFrame126("", 6024);
		c.getPA().sendFrame126("", 191);
		c.getPA().sendFrame126("", 15235);
		c.getPA().sendFrame126("", 15592);
		c.getPA().sendFrame126("", 6987);
		c.getPA().sendFrame126("", 15098);
		c.getPA().sendFrame126("", 15352);
		c.getPA().sendFrame126("", 18306);
		c.getPA().sendFrame126("", 15499);
		c.getPA().sendFrame126("", 668);
		c.getPA().sendFrame126("", 18684);
		c.getPA().sendFrame126("", 6027);
		c.getPA().sendFrame126("", 18157);
		c.getPA().sendFrame126("", 15847);
		c.getPA().sendFrame126("", 16128);
		c.getPA().sendFrame126("", 12836);
		c.getPA().sendFrame126("", 16149);
		c.getPA().sendFrame126("", 15841);
		c.getPA().sendFrame126("", 7353);
		c.getPA().sendFrame126("", 7358);
		c.getPA().sendFrame126("", 17510);
		c.getPA().sendFrame126("", 7359);
		c.getPA().sendFrame126("", 14169);
		c.getPA().sendFrame126("", 10115);
		c.getPA().sendFrame126("", 14604);
		c.getPA().sendFrame126("", 7360);
		c.getPA().sendFrame126("", 12282);
		c.getPA().sendFrame126("", 13577);
		c.getPA().sendFrame126("", 12839);
		c.getPA().sendFrame126("", 7361);
		c.getPA().sendFrame126("", 11857);
		c.getPA().sendFrame126("", 7362);
		c.getPA().sendFrame126("", 7363);
		c.getPA().sendFrame126("", 7364);
		c.getPA().sendFrame126("", 10135);
		c.getPA().sendFrame126("", 4508);
		c.getPA().sendFrame126("", 11907);
		c.getPA().sendFrame126("", 7365);
		c.getPA().sendFrame126("", 7366);
		c.getPA().sendFrame126("", 7367);
		c.getPA().sendFrame126("", 13389);
		c.getPA().sendFrame126("", 7368);
		c.getPA().sendFrame126("", 11132);
		c.getPA().sendFrame126("", 7369);
		c.getPA().sendFrame126("", 12389);
		c.getPA().sendFrame126("", 13974);
		c.getPA().sendFrame126("", 7370);
		c.getPA().sendFrame126("", 8137);
		c.getPA().sendFrame126("", 7371);
		c.getPA().sendFrame126("", 12345);
		c.getPA().sendFrame126("", 7372);
		c.getPA().sendFrame126("", 8115);
		c.getPA().sendFrame126("", 8576);
		c.getPA().sendFrame126("", 12139);
		c.getPA().sendFrame126("", 7373);
		c.getPA().sendFrame126("", 7374);
		c.getPA().sendFrame126("", 8969);
		c.getPA().sendFrame126("", 7375);
		c.getPA().sendFrame126("", 7376);
		c.getPA().sendFrame126("", 1740);
		c.getPA().sendFrame126("", 3278);
		c.getPA().sendFrame126("", 7378);
		c.getPA().sendFrame126("", 6518);
		c.getPA().sendFrame126("", 7379);
		c.getPA().sendFrame126("", 7380);
		c.getPA().sendFrame126("", 7381);
		c.getPA().sendFrame126("", 11858);
		c.getPA().sendFrame126("", 9927);
		c.getPA().sendFrame126("", 7349);
		c.getPA().sendFrame126("", 7350);
		c.getPA().sendFrame126("", 7351);
		c.getPA().sendFrame126("", 13356);
	/*END OF ALL QUESTS*/
}

	public void GAFinish() {
		c.getStart = 10;
		c.getPA().addSkillXP(15000, 7);
		c.getPA().showInterface(297);
		c.getPA().showInterface(12140);
		c.getPA().sendFrame126("You have completed: Cook's Assistant", 12144);
		c.getPA().sendFrame126("15,000 Cooking Experience", 12150);
		c.getPA().sendFrame126("", 12151);
		c.getPA().sendFrame126("", 12152);
		c.getPA().sendFrame126("", 12153);
		c.getPA().sendFrame126("", 12154);
		c.getPA().sendFrame126("", 12155);
		c.getPA().sendFrame126("@gre@Cook's Assistant", 7332);
	}
	
}
