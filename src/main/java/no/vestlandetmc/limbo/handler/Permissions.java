package no.vestlandetmc.limbo.handler;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class Permissions {

	private static final Map<String, Boolean> childrenAdmin = new HashMap<>();
	private static final Map<String, Boolean> childrenMod = new HashMap<>();

	public static final Permission LIMBO = new Permission("limbo.limbo", "Allows limbo command.", PermissionDefault.OP);
	public static final Permission NOTIFY = new Permission("limbo.notify", "Be notified.", PermissionDefault.OP);
	public static final Permission TEMPLIMBO = new Permission("limbo.templimbo", "Allows templimbo command.", PermissionDefault.OP);
	public static final Permission UNLIMBO = new Permission("limbo.unlimbo", "Allows unlimbo command.", PermissionDefault.OP);
	public static final Permission LIMBOLIST = new Permission("limbo.limbolist", "Allows limbolist command.", PermissionDefault.OP);
	public static final Permission CHATVISIBLE = new Permission("limbo.chatvisible", "See limbo chat.", PermissionDefault.OP);
	public static final Permission BYPASS = new Permission("limbo.bypass", "Can not be banished.", PermissionDefault.OP);

	public static final Permission MOD = new Permission("limbo.moderator", "Common commands for moderators.", PermissionDefault.OP, childrenMod);
	public static final Permission ADMIN = new Permission("limbo.admin", "Give you all commands.", PermissionDefault.OP, childrenAdmin);

	public static void register() {
		final PluginManager pm = Bukkit.getPluginManager();

		childrenAdmin.put("limbo.limbo", true);
		childrenAdmin.put("limbo.notify", true);
		childrenAdmin.put("limbo.templimbo", true);
		childrenAdmin.put("limbo.unlimbo", true);
		childrenAdmin.put("limbo.limbolist", true);
		childrenAdmin.put("limbo.chatvisible", true);
		childrenAdmin.put("limbo.bypass", true);

		childrenMod.put("limbo.limbo", true);
		childrenMod.put("limbo.notify", true);
		childrenMod.put("limbo.templimbo", true);
		childrenMod.put("limbo.unlimbo", true);
		childrenMod.put("limbo.limbolist", true);
		childrenMod.put("limbo.chatvisible", true);
		childrenMod.put("limbo.bypass", true);

		pm.addPermissions(List.of(
				LIMBO,
				NOTIFY,
				TEMPLIMBO,
				UNLIMBO,
				LIMBOLIST,
				CHATVISIBLE,
				BYPASS,
				MOD,
				ADMIN
		));
	}
}
