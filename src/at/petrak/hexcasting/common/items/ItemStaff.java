package at.petrak.hexcasting.common.items;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import java.util.List;

import kotlin.Triple;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemStaff extends Item {
    public static final ResourceLocation FUNNY_LEVEL_PREDICATE = new ResourceLocation("hexcasting", "funny_level");

    public ItemStaff(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> m_7203_(Level world, Player player, InteractionHand hand) {
        if (player.m_6144_())
            if (world.m_5776_()) {
                player.m_5496_(HexSounds.FAIL_PATTERN, 1.0F, 1.0F);
            } else if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
            }
        if (!world.m_5776_() && player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            CastingHarness harness = IXplatAbstractions.INSTANCE.getHarness(serverPlayer, hand);
            List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatterns(serverPlayer);
            Triple<List<CompoundTag>, List<CompoundTag>, CompoundTag> descs = harness.generateDescs();
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer, new MsgOpenSpellGuiAck(hand, patterns, descs
                    .getFirst(), descs.getSecond(), descs.getThird(), harness
                    .getParenCount()));
        }
        player.m_36246_(Stats.f_12982_.m_12902_(this));
        return InteractionResultHolder.m_19090_(player.m_21120_(hand));
    }
}

