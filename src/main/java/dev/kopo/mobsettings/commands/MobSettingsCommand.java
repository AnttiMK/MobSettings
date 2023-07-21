package dev.kopo.mobsettings.commands;

import dev.kopo.mobsettings.MobSettings;
import io.papermc.paper.configuration.WorldConfiguration;
import io.papermc.paper.configuration.WorldConfiguration.Entities.Spawning.DespawnRange;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.SpigotWorldConfig;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MobSettingsCommand implements TabExecutor {

    private final MobSettings plugin;

    public MobSettingsCommand(MobSettings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("pvprealm.admin")) {
            sender.sendRichMessage("<red>Ei oikeutta!");
            return true;
        }

        if (args.length == 0) {
            sender.sendPlainMessage("/mobsettings help");
        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendPlainMessage("MobSettings commands:");
            sender.sendPlainMessage("/mobsettings dump");
            sender.sendPlainMessage("/mobsettings set");
        } else if (args[0].equalsIgnoreCase("dump")) {
            dumpMobSpawnSettings(sender);
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                sender.sendPlainMessage("Usage: /mobsettings set <mobspawnrange|despawnrange|limit|ticks> <value> <category> [world]");
                return true;
            }

            if (args[1].equalsIgnoreCase("mobspawnrange")) {
                if (args.length < 3) {
                    sender.sendPlainMessage("Usage: /mobsettings set mobspawnrange <value> [world]");
                    return true;
                }

                int spawnRange;
                try {
                    spawnRange = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendPlainMessage("Spawn rangea ei tunnistettu numeroksi!");
                    return true;
                }
                if (spawnRange <= 0) {
                    sender.sendPlainMessage("Spawn range täytyy olla positiivinen luku!");
                    return true;
                }

                ServerLevel level;
                if (args.length == 4) {
                    CraftWorld world = ((CraftWorld) plugin.getServer().getWorld(args[3]));
                    if (world == null) {
                        sender.sendPlainMessage("Maailmaa ei löytynyt!");
                        return true;
                    }
                    level = world.getHandle();
                } else {
                    if (sender instanceof Player p) {
                        level = ((CraftWorld) p.getWorld()).getHandle();
                    } else {
                        sender.sendPlainMessage("Maailman nimi täytyy määritellä!");
                        return true;
                    }
                }

                int oldSpawnRange = level.spigotConfig.mobSpawnRange;
                level.spigotConfig.mobSpawnRange = (byte) spawnRange;
                sender.sendPlainMessage("Mob spawn range asetettu arvosta " + oldSpawnRange + " arvoon " + spawnRange + " maailmassa " + level.getWorld().getName());
            } else if (args[1].equalsIgnoreCase("limit")) {
                if (args.length < 4) {
                    sender.sendPlainMessage("Usage: /mobsettings set limit <value> <category> [world]");
                    return true;
                }

                int limit;
                try {
                    limit = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendPlainMessage("Spawn limitiä ei tunnistettu numeroksi!");
                    return true;
                }
                if (limit <= 0) {
                    sender.sendPlainMessage("Spawn limit täytyy olla positiivinen luku!");
                    return true;
                }

                SpawnCategory category;
                try {
                    category = SpawnCategory.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendPlainMessage("Spawn-kategoriaa ei tunnistettu!");
                    return true;
                }
                if (category == SpawnCategory.MISC) {
                    sender.sendPlainMessage("Misc-kategorian arvoa ei voi asettaa!");
                    return true;
                }

                CraftWorld world;
                if (args.length == 5) {
                    world = ((CraftWorld) plugin.getServer().getWorld(args[4]));
                    if (world == null) {
                        sender.sendPlainMessage("Maailmaa ei löytynyt!");
                        return true;
                    }
                } else {
                    if (sender instanceof Player p) {
                        world = (CraftWorld) p.getWorld();
                    } else {
                        sender.sendPlainMessage("Maailman nimi täytyy määritellä!");
                        return true;
                    }
                }

                int oldLimit = world.getSpawnLimit(category);
                world.setSpawnLimit(category, limit);
                sender.sendPlainMessage("Spawn limit asetettu arvosta " + oldLimit + " arvoon " + limit + " kategoriassa " + category.name() + " maailmassa " + world.getName());

            } else if (args[1].equalsIgnoreCase("ticks")) {
                if (args.length < 4) {
                    sender.sendPlainMessage("Usage: /mobsettings set ticks <value> <category> [world]");
                    return true;
                }

                int ticks;
                try {
                    ticks = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendPlainMessage("Ticksiä ei tunnistettu numeroksi!");
                    return true;
                }
                if (ticks <= 0) {
                    sender.sendPlainMessage("Ticks täytyy olla positiivinen luku!");
                    return true;
                }

                SpawnCategory category;
                try {
                    category = SpawnCategory.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendPlainMessage("Spawn-kategoriaa ei tunnistettu!");
                    return true;
                }
                if (category == SpawnCategory.MISC) {
                    sender.sendPlainMessage("Misc-kategorian arvoa ei voi asettaa!");
                    return true;
                }

                CraftWorld world;
                if (args.length == 5) {
                    world = ((CraftWorld) plugin.getServer().getWorld(args[4]));
                    if (world == null) {
                        sender.sendPlainMessage("Maailmaa ei löytynyt!");
                        return true;
                    }
                } else {
                    if (sender instanceof Player p) {
                        world = (CraftWorld) p.getWorld();
                    } else {
                        sender.sendPlainMessage("Maailman nimi täytyy määritellä!");
                        return true;
                    }
                }

                long oldTicks = world.getTicksPerSpawns(category);
                world.setTicksPerSpawns(category, ticks);
                sender.sendPlainMessage("Kategorian " + category.name() + " ticksPerSpawn asetettu arvosta " + oldTicks + " arvoon " + ticks + " maailmassa " + world.getName());
            } else if (args[1].equalsIgnoreCase("despawnrange")) {
                if (args.length < 5) {
                    sender.sendPlainMessage("Usage: /mobsettings set despawnrange <category> <soft|hard> <value> [world]");
                    return true;
                }

                MobCategory category;
                try {
                    category = MobCategory.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendPlainMessage("Spawn-kategoriaa ei tunnistettu!");
                    return true;
                }

                boolean soft = args[3].equalsIgnoreCase("soft");
                boolean hard = args[3].equalsIgnoreCase("hard");
                if (!soft && !hard) {
                    sender.sendPlainMessage("Usage: /mobsettings set despawnrange <soft|hard> <value> [world]");
                    return true;
                }

                int value;
                try {
                    value = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendPlainMessage("Despawn rangea ei tunnistettu numeroksi!");
                    return true;
                }
                if (value <= 0) {
                    sender.sendPlainMessage("Despawn range täytyy olla positiivinen luku!");
                    return true;
                }

                ServerLevel level;
                if (args.length == 6) {
                    CraftWorld world = ((CraftWorld) plugin.getServer().getWorld(args[5]));
                    if (world == null) {
                        sender.sendPlainMessage("Maailmaa ei löytynyt!");
                        return true;
                    }
                    level = world.getHandle();
                } else {
                    if (sender instanceof Player p) {
                        level = ((CraftWorld) p.getWorld()).getHandle();
                    } else {
                        sender.sendPlainMessage("Maailman nimi täytyy määritellä!");
                        return true;
                    }
                }

                DespawnRange oldDespawnRange = level.paperConfig().entities.spawning.despawnRanges.get(category);
                if (soft) {
                    level.paperConfig().entities.spawning.despawnRanges.put(category, new DespawnRange(value, oldDespawnRange.hard()));
                    sender.sendPlainMessage("Kategorian " + category.name() + " soft despawn range asetettu arvosta " + oldDespawnRange.soft() + " arvoon " + value + " maailmassa " + level.getWorld().getName());
                } else {
                    level.paperConfig().entities.spawning.despawnRanges.put(category, new DespawnRange(oldDespawnRange.soft(), value));
                    sender.sendPlainMessage("Kategorian " + category.name() + " hard despawn range asetettu arvosta " + oldDespawnRange.hard() + " arvoon " + value + " maailmassa " + level.getWorld().getName());
                }
            } else {
                sender.sendPlainMessage("Unknown command. Type \"/mobsettings help\" for help.");
            }
            return true;
        } else {
            sender.sendPlainMessage("Unknown command. Type \"/mobsettings help\" for help.");
            return true;
        }
        return true;
    }

    private void dumpMobSpawnSettings(CommandSender sender) {

        sender.sendPlainMessage("--- Global spawn limits/ticks per category ---");
        Server server = plugin.getServer();

        for (SpawnCategory category : SpawnCategory.values()) {
            if (category == SpawnCategory.MISC) continue;
            sender.sendPlainMessage(category.name() + ": " + server.getSpawnLimit(category) + " / " + server.getTicksPerSpawns((category)));
        }

        if (sender instanceof Player p) {
            sender.sendPlainMessage("\n--- Settings for current world (" + p.getWorld().getName() + ") ---");
            CraftWorld world = (CraftWorld) p.getWorld();
            ServerLevel level = world.getHandle();
            SpigotWorldConfig config = level.spigotConfig;
            WorldConfiguration paperConfig = level.paperConfig();

            sender.sendPlainMessage("\n   limits/ticks per category ---");
            for (SpawnCategory category : SpawnCategory.values()) {
                if (category == SpawnCategory.MISC) continue;
                sender.sendPlainMessage(category.name() + ": " + world.getSpawnLimit(category) + " / " + world.getTicksPerSpawns((category)));
            }

            sender.sendPlainMessage("\n   Despawn ranges:");
            for (MobCategory category : MobCategory.values()) {
                DespawnRange despawnRange = paperConfig.entities.spawning.despawnRanges.get(category);
                sender.sendPlainMessage("      " + category.name() + ": HARD=" + despawnRange.hard() + ", SOFT=" + despawnRange.soft());
            }

            sender.sendPlainMessage("mob-spawn-range: " + config.mobSpawnRange);
        } else {
            for (World world : server.getWorlds()) {
                sender.sendPlainMessage("\n--- Settings for world " + world.getName() + " ---");
                ServerLevel level = ((CraftWorld) world).getHandle();
                SpigotWorldConfig config = level.spigotConfig;
                WorldConfiguration paperConfig = level.paperConfig();
                sender.sendPlainMessage("\n   Despawn ranges:");
                for (MobCategory category : MobCategory.values()) {
                    DespawnRange despawnRange = paperConfig.entities.spawning.despawnRanges.get(category);
                    sender.sendPlainMessage("      " + category.name() + ": HARD=" + despawnRange.hard() + ", SOFT=" + despawnRange.soft());
                }
                sender.sendPlainMessage("mob-spawn-range: " + config.mobSpawnRange);
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("help", "dump", "set").filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (args.length >= 3) {
                    if (args[1].equalsIgnoreCase("limit") || args[1].equalsIgnoreCase("ticks")) {
                        if (args.length >= 4) {
                            if (args.length == 5) {
                                return Bukkit.getWorlds().stream().map(World::getName).filter(s -> s.startsWith(args[4].toLowerCase())).toList();
                            }
                            return Arrays.stream(SpawnCategory.values())
                                    .filter(cat -> cat != SpawnCategory.MISC)
                                    .map(cat -> cat.name().toLowerCase())
                                    .filter(s -> s.startsWith(args[3].toLowerCase()))
                                    .toList();
                        }
                        return List.of("<value>");
                    } else if (args[1].equalsIgnoreCase("despawnrange")) {
                        if (args.length >= 4) {
                            if (args.length >= 5) {
                                if (args.length == 6) {
                                    return Bukkit.getWorlds().stream().map(World::getName).filter(s -> s.startsWith(args[4].toLowerCase())).toList();
                                }
                                return List.of("<value>");
                            }
                            return Stream.of("hard", "soft").filter(s -> s.startsWith(args[3].toLowerCase())).toList();
                        }
                        return Arrays.stream(MobCategory.values())
                                .map(cat -> cat.name().toLowerCase())
                                .filter(s -> s.startsWith(args[3].toLowerCase()))
                                .toList();
                    } else if (args[1].equalsIgnoreCase("mobspawnrange")) {
                        if (args.length >= 4) {
                            return Bukkit.getWorlds().stream().map(World::getName).filter(s -> s.startsWith(args[3].toLowerCase())).toList();
                        }
                        return List.of("<value>");
                    }
                }
                return Stream.of("despawnrange", "limit", "mobspawnrange", "ticks").filter(s -> s.startsWith(args[1].toLowerCase())).toList();
            }
            return null;
        }
        return List.of();
    }
}
