package at.petrak.hexcasting.common.items;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import java.util.List;
import java.util.UUID;

import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import kotlin.Triple;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.Nullable;

public class ItemStaff extends ItemMediaHolder {
    public static int CONVERT_RATIO = MediaConstants.DUST_UNIT;

    public static final ResourceLocation FUNNY_LEVEL_PREDICATE = new ResourceLocation("hexcasting", "funny_level");

    public ItemStaff(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> m_7203_(Level world, Player player, InteractionHand hand) {
        ItemStack item = player.m_21120_(hand);
        var data = new StaffOwnerData(item);
        data.setOwner(player);
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

    @Nullable Player getPlayer(ItemStack stack) {
        return new StaffOwnerData(stack).getOwner();
    }

    @Override
    public int getMedia(ItemStack stack) {
        var player = getPlayer(stack);
        if (player == null) return 0;
        return (int) (ManaUtil.getCurrentMana(player) * CONVERT_RATIO);
    }

    @Override
    public int getMaxMedia(ItemStack stack) {
        var player = getPlayer(stack);
        if (player == null) return 0;
        return (ManaUtil.getMaxMana(player) * CONVERT_RATIO);
    }

    @Override
    public void setMedia(ItemStack stack, int i) {
        var player = getPlayer(stack);
        CapabilityRegistry.getMana(player).ifPresent(mana -> mana.setMana(((double) i / (double) CONVERT_RATIO)));
    }

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canRecharge(ItemStack itemStack) {
        return false;
    }

    static {
        StaffOwnerData.InitServerCatcher();
    }

    public static class StaffOwnerData extends ItemstackData {
        private UUID ownerUUID;
        private static MinecraftServer theServer;

        public static void InitServerCatcher() {
            var bus = MinecraftForge.EVENT_BUS;
            bus.addListener((ServerStartedEvent e) -> {
                theServer = e.getServer();
            });
            bus.addListener((ServerStoppedEvent e) -> {
                theServer = null;
            });
        }

        public StaffOwnerData(ItemStack stack) {
            super(stack);
            var tag = getItemTag(stack);
            try {
                ownerUUID = tag.m_128342_("owner");
            } catch (Exception e) {
                ownerUUID = new UUID(114, 514);
            }
        }

        @Override
        public String getTagString() {
            return "hex_staff";
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            tag.m_128362_("owner", ownerUUID);
        }

        @Nullable
        public ServerPlayer getOwner() {
            if (theServer == null) return null;
            return theServer.m_6846_().m_11259_(ownerUUID);
        }

        public void setOwner(Player player) {
            this.ownerUUID = player.m_20148_();
            writeItem();
        }
    }
}

