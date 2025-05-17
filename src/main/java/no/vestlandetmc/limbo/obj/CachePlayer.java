package no.vestlandetmc.limbo.obj;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.UUID;

public class CachePlayer {

	private final UUID uuid;
	@Getter
	private final long timestamp;
	@Getter
	private final long expire;
	@Getter
	private final String reason;
	@Getter
	private final UUID staffUUID;


	public CachePlayer(UUID uuid, UUID staffUUID, long timestamp, long expire, String reason) {
		this.uuid = uuid;
		this.staffUUID = staffUUID;
		this.timestamp = timestamp;
		this.expire = expire;
		this.reason = reason;
	}

	public String getName() {
		return Bukkit.getOfflinePlayer(this.uuid).getName();
	}

	public UUID getUniqueId() {
		return this.uuid;
	}

}
