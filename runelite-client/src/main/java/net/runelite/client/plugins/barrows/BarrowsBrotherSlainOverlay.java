/*
 * Copyright (c) 2018, Seth <http://github.com/sethtroll>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.barrows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.api.NpcID.*;

@Slf4j
public class BarrowsBrotherSlainOverlay extends Overlay
{
	private final Client client;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	private BarrowsBrotherSlainOverlay(Client client)
	{
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Do not display overlay if potential is null/hidden
		Widget potential = client.getWidget(WidgetInfo.BARROWS_POTENTIAL);
		if (potential == null || potential.isHidden())
		{
			return null;
		}

		// Hide original overlay
		Widget barrowsBrothers = client.getWidget(WidgetInfo.BARROWS_BROTHERS);
		if (barrowsBrothers != null)
		{
			barrowsBrothers.setHidden(true);
		}

		int id = engagedWith();
		switch(id)
		{
			case AHRIM_THE_BLIGHTED:
			case DHAROK_THE_WRETCHED:
			case GUTHAN_THE_INFESTED:
			case KARIL_THE_TAINTED:
			case TORAG_THE_CORRUPTED:
			case VERAC_THE_DEFILED:
				break;
			default:
				id = -1;
		}

		panelComponent.getChildren().clear();

		for (BarrowsBrothers brother : BarrowsBrothers.values())
		{
			String slain = client.getVar(brother.getKilledVarbit()) > 0 ? "✓" : "";
			panelComponent.getChildren().add(LineComponent.builder()
				.left(brother.getName())
				.leftColor((id != -1 && id == brother.getId()) ? Color.ORANGE : Color.WHITE)
				.right(slain)
				.rightColor(slain.isEmpty() ? Color.WHITE : Color.GREEN)
				.build());
		}

		return panelComponent.render(graphics);
	}

	/**
	 * Actors currently engaged with the player
	 * @return ID of any actor currently interacting with the player
	 */
	private int engagedWith()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return -1;
		}

		Actor actor = player.getInteracting();
		if (actor == null || !(actor instanceof NPC))
		{
			return -1;
		}

		NPC opponent = (NPC) actor;

		// Only need to grab the first word of the string
		return opponent.getId();
	}
}
