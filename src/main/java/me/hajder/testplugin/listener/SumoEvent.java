package me.hajder.testplugin.listener;

import me.hajder.testplugin.SumoPlugin;
import me.hajder.testplugin.settings.SumoSettings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.hajder.testplugin.util.Utils.colorComponent;
import static me.hajder.testplugin.util.Utils.spawnFirework;

public final class SumoEvent implements Listener {
    private final SumoPlugin plugin;
    private static SumoEvent instance;
    private static boolean sumoInProgress = false;
    private List<Player> allPlayers = new ArrayList<>(); // all players not lost
    private List<Player> nextRound = new ArrayList<>(); // players gone to next round
    private int sumoRound = 0;
    private double playerLowestY;
    private Location sumoWorld;
    private Location sumoPlayerWorld1;
    private Location sumoPlayerWorld2;
    private Player sumoPlayer1;
    private Player sumoPlayer2;
    private String nextPlayerMsg;
    private String lossMessage;
    private String winnerMsg;
    private String startingMsg;
    private String glassCountdownMsg;
    private int startingCountdown;
    private int glassCountdown;

    private SumoEvent(SumoPlugin plugin) {
        this.plugin = plugin;
    }

    public static SumoEvent getInstance(SumoPlugin plugin) {
        if (instance == null) {
            instance = new SumoEvent(plugin);
        }
        return instance;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (sumoInProgress) {
            Player player = event.getPlayer();
            if (sumoPlayer1 == player || sumoPlayer2 == player) {
                if (player.getLocation().getY() < playerLowestY) {
                    player.sendMessage(colorComponent(lossMessage));
                    player.teleport(sumoWorld);

                    if (sumoPlayer1 == player) {
                        nextRound.add(sumoPlayer2);
                        sumoPlayer2.teleport(sumoWorld);
                        sumoPlayer1 = null;
                    } else {
                        nextRound.add(sumoPlayer1);
                        sumoPlayer1.teleport(sumoWorld);
                        sumoPlayer2 = null;
                    }

                    startRound();
                }
            }

        }
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (sumoInProgress) {
            if (event.getEntity() instanceof Player) {
                Player p = (Player) event.getEntity();
                if (p == sumoPlayer1 || p == sumoPlayer2) {
                    event.setCancelled(false);
                    event.setDamage(0.0D);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        allPlayers.remove(p);
        nextRound.remove(p);
    }

    public void setupSumo()
    {

        playerLowestY = (Double) SumoSettings.getInstance().get("lowestY");
        glassCountdownMsg = (String) SumoSettings.getInstance().get("messages.countdown");
        winnerMsg = (String) SumoSettings.getInstance().get("messages.winner");
        nextPlayerMsg = (String) SumoSettings.getInstance().get("messages.nextPlayer");
        startingMsg = (String) SumoSettings.getInstance().get("messages.starting");
        startingCountdown = (int) SumoSettings.getInstance().get("startingCountdown");
        glassCountdown = (int) SumoSettings.getInstance().get("glassCountdown");
        lossMessage = (String) SumoSettings.getInstance().get("messages.lost");

        sumoWorld = getLocations("spawn");
        sumoPlayerWorld1 = getLocations("player1");
        sumoPlayerWorld2 = getLocations("player2");


        nextRound.clear();
        allPlayers.clear();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (startingCountdown > 0) {
                    String msg = startingMsg.replace("%s", Integer.toString(startingCountdown));
                    Bukkit.getServer().sendMessage(colorComponent(msg));
                    startingCountdown--;
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.teleport(sumoWorld);
                        allPlayers.add(player);
                    }

                    this.cancel();
                    startRound();
                }
            }
        }.runTaskTimer(SumoPlugin.getInstance(), 20L,20L); // 20 ticks per second

        sumoInProgress = true;
    }

    private void startRound() {
        if (allPlayers.isEmpty()) {
            if (nextRound.size() > 1) {
                // New sumo round
                allPlayers = new ArrayList<>(nextRound);
                nextRound.clear();
                sumoRound++;
            } else if (nextRound.size() == 1) {
                // One player left, declare winner
                Player winner = nextRound.get(0);
                String msg = winnerMsg.replace("%p", winner.getName());
                Bukkit.getServer().sendMessage(colorComponent(msg));
                spawnFirework(winner.getLocation(), Color.RED, Color.GREEN);
                sumoInProgress = false;
                return;
            }
        }

        // Check if there are enough players for the next round
        if (allPlayers.size() >= 2) {
            sumoPlayer1 = allPlayers.get(0);
            sumoPlayer2 = allPlayers.get(1);
            allPlayers.remove(sumoPlayer1);
            allPlayers.remove(sumoPlayer2);

            glassBlocks(sumoPlayerWorld2, true);
            glassBlocks(sumoPlayerWorld1, true);
            sumoPlayer1.teleport(sumoPlayerWorld1);
            sumoPlayer2.teleport(sumoPlayerWorld2);

            new BukkitRunnable() {
                int g = glassCountdown;
                @Override
                public void run() {
                    if (g == 0) {
                        glassBlocks(sumoPlayerWorld1, false);
                        glassBlocks(sumoPlayerWorld2, false);
                        this.cancel();
                    } else {
                        String msg = glassCountdownMsg.replace("%s", Integer.toString(g));
                        sumoPlayer1.sendMessage(colorComponent(msg));
                        sumoPlayer2.sendMessage(colorComponent(msg));
                        g--;
                    }
                }
            }.runTaskTimer(SumoPlugin.getInstance(), 20L, 20L); // 20 ticks per second, 3 seconds

        } else if (allPlayers.size() == 1) {
            // One player left, move to next round
            nextRound.add(allPlayers.get(0));
            allPlayers.clear();
            startRound();
        }
    }

    public Location getLocations(String location) {
        double x = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".X");
        double y = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".Y");
        double z = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".Z");

        double yaw = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".Yaw");
        double pitch = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".Pitch");

        World world = Bukkit.getWorld(SumoSettings.getInstance().get("sumo-cords." + location + ".World").toString());
        Location getLocation = new Location(world, x, y, z);

        getLocation.setYaw((float) yaw);
        getLocation.setPitch((float) pitch);

        return getLocation;
    }

    private void glassBlocks(Location location, boolean setBlocks) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Determine the material based on the action
        Material glassMaterial = setBlocks ? Material.GLASS : Material.AIR;

        // Modify glass blocks around the location
        world.getBlockAt(x, y+3, z).setType(glassMaterial);

        world.getBlockAt(x + 1, y, z).setType(glassMaterial);
        world.getBlockAt(x + 1, y+1, z).setType(glassMaterial);
        world.getBlockAt(x + 1, y+2, z).setType(glassMaterial);

        world.getBlockAt(x - 1, y, z).setType(glassMaterial);
        world.getBlockAt(x - 1, y+1, z).setType(glassMaterial);
        world.getBlockAt(x - 1, y+2, z).setType(glassMaterial);

        world.getBlockAt(x, y, z + 1).setType(glassMaterial);
        world.getBlockAt(x, y+1, z + 1).setType(glassMaterial);
        world.getBlockAt(x, y+2, z + 1).setType(glassMaterial);

        world.getBlockAt(x, y, z - 1).setType(glassMaterial);
        world.getBlockAt(x, y+1, z - 1).setType(glassMaterial);
        world.getBlockAt(x, y+2, z - 1).setType(glassMaterial);
    }

    public String getDebug() {
        String p1 = "Null";
        String p2 = "Null";
        int ap = 0;
        int np = 0;

        if (sumoPlayer2 != null)
            p2 = sumoPlayer2.getName();

        if (sumoPlayer1 != null)
            p1 = sumoPlayer1.getName();

        if (allPlayers != null)
            ap = allPlayers.size();
        if (nextRound != null)
            ap = nextRound.size();

        return "<green>Debug information: " + "\nOnline players: " + "\nAll players list: " + ap + "\nNext round list: " + np + "\nPlayer1: " + p1 + "\nPlayer2: " + p2;
    }

    public boolean sumoProgress() {
        SumoPlugin.getInstance().getLogger().info("Sumo progress = " + sumoInProgress);
        return sumoInProgress;
    }

    public String forceAdd(Player p) {
        if (sumoInProgress && !isPlayerInSumo(p)) {
            if (canAddPlayerToSumo(p)) {
                allPlayers.add(p);
                return "<green>Successfully added " + p.getName() + " to this round.";
            }
            return "<red>Player is currently standing on the platform lil bro...";
        }
        return "<orange>Player is either already in-game, or the sumo event has not started!";
    }

    private boolean isPlayerInSumo(Player player) {
        return allPlayers.contains(player) || nextRound.contains(player);
    }

    private boolean canAddPlayerToSumo(Player player) {
        return sumoPlayer1 != null && sumoPlayer2 != null && (sumoPlayer1 != player || sumoPlayer2 != player);
    }
}