package dev.nottekk.notvolt.services;

import net.dv8tion.jda.api.Permission;

public final class PermissionService {
	private PermissionService() {}

	public static boolean hasModerateMembers(net.dv8tion.jda.api.entities.Member member) {
		return member.hasPermission(Permission.MODERATE_MEMBERS);
	}

	public static boolean hasManageMessages(net.dv8tion.jda.api.entities.Member member) {
		return member.hasPermission(Permission.MESSAGE_MANAGE) || member.hasPermission(Permission.MANAGE_MESSAGES);
	}
}
