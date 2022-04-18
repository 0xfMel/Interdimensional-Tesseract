package ftm._0xfmel.itdmtrct.client.screen;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import com.mojang.blaze3d.matrix.MatrixStack;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import ftm._0xfmel.itdmtrct.network.SelectChannelMessage;
import ftm._0xfmel.itdmtrct.utils.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TesseractScreen extends ContainerScreen<TesseractContainer> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(ModGlobals.MOD_ID,
            "textures/gui/tesseract.png");

    public static int prevSelectedChannel = -1;

    protected ITesseractChannels channels;

    protected int scrollbarHeight = 95;
    protected int scrollHeight = this.scrollbarHeight - 15;
    protected int scrollAreaHeight = 93;

    protected int scrollShowOffset = 0;
    protected int scrollPosOffset = 0;

    protected int scrollbarPosX = 155;
    protected int scrollbarPosY = 38;

    protected int channelHeight = 16;
    protected int maxChannels = Math.floorDiv(this.scrollAreaHeight, this.channelHeight);

    protected float scrollPos = 0;
    protected boolean scrolling = false;

    protected int channelPosX = 9;
    protected int channelPosY = 40;

    protected Button newBtn;
    protected Button confirmBtn;
    protected Button cancelBtn;
    protected Button editBtn;
    protected Button saveBtn;

    protected boolean isEditing = false;

    protected boolean hasOwnChannel = false;

    protected TextFieldWidget name;
    protected PrivateToggleButton isPrivate;

    protected int selectedChannel = -1;

    protected boolean adding = false;

    protected List<TesseractChannel> orderedChannels;

    public TesseractScreen(TesseractContainer p_create_1_, PlayerInventory p_create_2_, ITextComponent p_create_3_) {
        super(p_create_1_, p_create_2_, p_create_3_);
    }

    @Override
    protected void init() {
        super.init();

        World world = this.minecraft.level;
        this.channels = world
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .orElseGet(() -> {
                    Logging.LOGGER.warn("No TesseractChannels capability on world, using default");
                    return new ITesseractChannels.TesseractChannels(world.isClientSide);
                });

        this.selectedChannel = TesseractScreen.prevSelectedChannel;

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        ITextComponent addBtnText = new TranslationTextComponent("container.tesseract.new");
        int addBtnWidth = this.font.width(addBtnText) + 12;
        this.newBtn = new Button(this.leftPos + 169 - addBtnWidth, this.topPos + 16, addBtnWidth, 20, addBtnText,
                this::handleButtonClick);

        ITextComponent editBtnText = new TranslationTextComponent("container.tesseract.edit");
        int editBtnWidth = this.font.width(editBtnText) + 12;
        this.editBtn = new Button(this.leftPos + 169 - editBtnWidth, this.topPos + 16, editBtnWidth, 20, editBtnText,
                this::handleButtonClick);
        this.editBtn.visible = false;

        this.confirmBtn = new Button(this.leftPos + 7, this.topPos + 139, (this.imageWidth - 20) / 2, 20,
                new TranslationTextComponent("container.confirm"), this::handleButtonClick);

        this.cancelBtn = new Button(this.leftPos + (this.imageWidth - 20) / 2 + 13, this.topPos + 139,
                (this.imageWidth - 20) / 2, 20,
                new TranslationTextComponent("container.cancel"), this::handleButtonClick);

        this.saveBtn = new Button(this.leftPos + 7, this.topPos + 139, (this.imageWidth - 20) / 2, 20,
                new TranslationTextComponent("container.save"), this::handleButtonClick);
        this.saveBtn.visible = false;

        this.addButton(this.newBtn);
        this.addButton(this.editBtn);
        this.addButton(this.confirmBtn);
        this.addButton(this.cancelBtn);
        this.addButton(this.saveBtn);

        this.name = new TextFieldWidget(this.font, this.leftPos + 10, this.topPos + 55, 158, 12,
                new TranslationTextComponent("container.tesseract.name"));
        this.name.setTextColor(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(20);
        this.children.add(this.name);

        this.isPrivate = new PrivateToggleButton(this.leftPos + 7, this.topPos + 71);
        this.children.add(this.isPrivate);
    }

    private void setDefaultValues() {
        String name = "";
        boolean isPrivate = false;

        if (this.menu.getOwnChannel()) {
            TesseractChannel channel = this.channels.getChannel(this.menu.getChannelId());
            if (channel.playerUuid != this.minecraft.player.getUUID())
                return;

            name = channel.name;
            isPrivate = channel.isPrivate;
        }

        this.name.setValue(name);
        this.isPrivate.setValue(isPrivate);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        String s = this.name.getValue();
        this.init(pMinecraft, pWidth, pHeight);
        this.name.setValue(s);
    }

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return !this.name.keyPressed(pKeyCode, pScanCode, pModifiers) && !this.name.canConsumeInput()
                ? super.keyPressed(pKeyCode, pScanCode, pModifiers)
                : true;
    }

    private void handleButtonClick(Button button) {
        if (button == this.newBtn || button == this.editBtn) {
            this.setDefaultValues();
            this.isEditing = true;
        } else if (button == this.confirmBtn) {
            if (TesseractScreen.prevSelectedChannel != this.selectedChannel) {
                ModPacketHander.INSTANCE.sendToServer(new SelectChannelMessage(this.selectedChannel));
            }
            this.minecraft.player.closeContainer();
        } else if (button == this.cancelBtn) {
            if (this.isEditing) {
                this.isEditing = false;
            } else {
                this.minecraft.player.closeContainer();
            }
        } else if (button == this.saveBtn) {
            try {
                if (!this.menu.getOwnChannel()) {
                    this.scrollPos = 0;
                    this.adding = true;
                    this.channels.addChannel(
                            new TesseractChannel(-1, this.name.getValue(), this.isPrivate.getValue()));
                } else {
                    this.channels.modifyChannel(this.menu.getChannelId(), (channel) -> {
                        channel.name = this.name.getValue();
                        channel.isPrivate = this.isPrivate.getValue();
                    });
                }
                this.isEditing = false;
            } catch (ValidationException e) {
                Logging.LOGGER.warn(e.getMessage());
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.name.tick();
    }

    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.hasOwnChannel = this.menu.getOwnChannel();

        int len = this.channels.getChannelsSize();

        boolean canEdit = !this.hasOwnChannel
                || (this.channels.getChannel(this.menu.getChannelId()).playerUuid == this.minecraft.player.getUUID());

        this.orderedChannels = this.channels.getChannels()
                .sorted((a, b) -> this.canSelectChannel(a) ? this.canSelectChannel(b) ? 0 : -1 : 1)
                .sorted((a, b) -> a.id == TesseractScreen.prevSelectedChannel ? -1
                        : b.id == TesseractScreen.prevSelectedChannel ? 1 : 0)
                .collect(Collectors.toList());

        if (TesseractScreen.prevSelectedChannel == this.selectedChannel || this.adding) {
            TesseractScreen.prevSelectedChannel = this.selectedChannel = this.menu.getChannelId();

            if (this.adding) {
                this.adding = false;
            }
        }

        this.confirmBtn.visible = !this.isEditing;
        this.saveBtn.visible = this.isEditing;
        this.newBtn.visible = !this.isEditing && !this.hasOwnChannel && canEdit;
        this.editBtn.visible = !this.isEditing && this.hasOwnChannel && canEdit;

        this.setFocused(this.isEditing ? this.name : null);
        this.name.setFocus(this.isEditing);
        this.name.setVisible(this.isEditing);

        this.isPrivate.visible = this.isEditing;

        if (len > this.maxChannels) {
            int extraChannels = len - this.maxChannels;
            int newScrollShowOffset = Math.round(MathHelper.lerp(this.scrollPos, 0, extraChannels));

            if (newScrollShowOffset != this.scrollShowOffset) {
                if (newScrollShowOffset == 0 || newScrollShowOffset < this.scrollShowOffset) {
                    this.scrollPosOffset = 0;
                } else {
                    this.scrollPosOffset = this.scrollAreaHeight - (this.maxChannels * 16);
                }
            }
            this.scrollShowOffset = newScrollShowOffset;
        }

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        this.renderFg(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
    }

    @Override
    protected void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
        this.renderBackground(pMatrixStack);
        this.minecraft.getTextureManager().bind(TesseractScreen.TEXTURE);
        this.blit(pMatrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.isEditing) {
            // name input
            this.blit(pMatrixStack, this.leftPos + 7, this.topPos + 51, 0, 203, 162, 16);
        } else {
            // left half brown bg
            this.blit(pMatrixStack, this.leftPos + 7, this.topPos + 38, 176, 15, 66, 97);
            // middle verticle line brown bg
            this.blit(pMatrixStack, this.leftPos + 7 + 66, this.topPos + 38, 177, 15, 1, 97);
            // right half brown bg
            this.blit(pMatrixStack, this.leftPos + 74, this.topPos + 38, 176, 112, 80, 97);

            // scroll bar bg
            this.blit(pMatrixStack,
                    this.leftPos + this.scrollbarPosX,
                    this.topPos + this.scrollbarPosY,
                    242, 15,
                    14, 97);

            // scroll pos
            if (this.channels.getChannelsSize() > this.maxChannels) {
                this.blit(pMatrixStack,
                        this.leftPos + this.scrollbarPosX + 1,
                        this.topPos + this.scrollbarPosY + 1 + Math.round(this.scrollPos * this.scrollHeight),
                        232, 0,
                        12, 15);
            } else {
                this.blit(pMatrixStack,
                        this.leftPos + this.scrollbarPosX + 1,
                        this.topPos + this.scrollbarPosY + 1,
                        244, 0,
                        12, 15);
            }

            if (this.selectedChannel >= 0) {
                TesseractChannel selected = this.channels.getChannel(this.selectedChannel);
                int selectedIndex = this.orderedChannels.indexOf(selected);
                if (selectedIndex >= this.scrollShowOffset
                        && selectedIndex < this.maxChannels + this.scrollShowOffset) {

                    this.blit(pMatrixStack,
                            this.leftPos + this.channelPosX,
                            this.topPos + this.channelPosY + (16 * (selectedIndex - this.scrollShowOffset))
                                    + this.scrollPosOffset,
                            0, 166,
                            143, 15);
                }
            }
        }
    }

    protected void renderFg(MatrixStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        if (this.isEditing) {
            this.name.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
            this.isPrivate.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
        }
    }

    @Override
    protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY) {
        this.font.draw(pMatrixStack, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2,
                6, 4210752);

        if (this.isEditing) {
            this.font.draw(pMatrixStack,
                    new TranslationTextComponent(
                            this.hasOwnChannel ? "container.tesseract.editing" : "container.tesseract.editingnew"),
                    8, 28, 4210752);

            this.font.draw(pMatrixStack,
                    new TranslationTextComponent("container.tesseract.name"), 8, 41, 4210752);

            this.font.draw(pMatrixStack, new TranslationTextComponent("container.tesseract.private"), 34, 78, 4210752);
        } else {
            this.font.draw(pMatrixStack, new TranslationTextComponent("container.tesseract.available"), 8, 28, 4210752);

            for (int i = 0; i < Math.min(this.channels.getChannelsSize(), this.maxChannels); i++) {
                TesseractChannel channel = this.orderedChannels.get(i + this.scrollShowOffset);
                this.font.draw(
                        pMatrixStack,
                        channel.name,
                        this.channelPosX + 4,
                        this.channelPosY + 4 + (16 * i) + this.scrollPosOffset,
                        this.selectedChannel == channel.id ? -1 : this.canSelectChannel(channel) ? 15592941 : 7500402);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            if (pMouseY > this.topPos + this.scrollbarPosY + 1
                    && pMouseY < this.topPos + this.scrollbarPosY + this.scrollbarHeight + 1
                    && pMouseX > this.leftPos + this.scrollbarPosX + 1
                    && pMouseX < this.leftPos + this.scrollbarPosX + 13) {
                this.scrolling = this.channels.getChannelsSize() > maxChannels;

                if (this.scrolling) {
                    this.setScrollPos(pMouseY);
                }

                return true;
            }

            if (pMouseY > this.topPos + this.channelPosY
                    && pMouseY < this.topPos + this.channelPosY + this.scrollAreaHeight
                    && pMouseX > this.leftPos + this.channelPosX
                    && pMouseX < this.leftPos + this.channelPosX + 143) {

                int y = (int) Math.floor(pMouseY)
                        - this.topPos
                        - this.channelPosY
                        // - this.scrollShowOffset
                        - this.scrollPosOffset;
                int selectPos = Math.floorDiv(y, 16) + this.scrollShowOffset;
                if (y % 16 != 15 && selectPos < this.channels.getChannelsSize()) {
                    this.selectedChannel = this.orderedChannels.get(selectPos).id;
                    return true;
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.scrolling = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if (this.scrolling) {
            this.setScrollPos(pMouseY);
        }

        super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == 0 && this.scrolling) {
            return true;
        }

        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private void setScrollPos(double pMouseY) {
        this.scrollPos = MathHelper.clamp(
                (float) MathHelper.inverseLerp(
                        pMouseY - this.topPos - this.scrollbarPosY - 7.5,
                        0,
                        this.scrollHeight),
                0, 1);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int channelsLen = this.channels.getChannelsSize();
        if (channelsLen > this.maxChannels) {
            int i = channelsLen - this.maxChannels;
            this.scrollPos = MathHelper.clamp(
                    (float) ((double) this.scrollPos - pDelta / (double) i),
                    0, 1);
        }

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    private boolean canSelectChannel(TesseractChannel channel) {
        return (channel.inRange && channel.inValidDimension) || channel.id == this.selectedChannel;
    }

    @OnlyIn(Dist.CLIENT)
    protected class PrivateToggleButton extends AbstractButton {
        private boolean mouseDown;
        private boolean value;

        protected PrivateToggleButton(int pX, int pY) {
            super(pX, pY, 22, 22, StringTextComponent.EMPTY);
        }

        public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
            Minecraft.getInstance().getTextureManager().bind(TesseractScreen.TEXTURE);
            int i = 181;
            int j = 0;
            if (this.mouseDown) {
                j += this.width * 1;
            } else if (this.isHovered()) {
                j += this.width * 2;
            }

            this.blit(pMatrixStack, this.x, this.y, j, i, this.width, this.height);
            this.renderIcon(pMatrixStack);
        }

        protected void renderIcon(MatrixStack pPoseStack) {
            int i = 68;
            int j = 182;
            if (!this.value) {
                i = 90;
            }

            this.blit(pPoseStack, this.x + 2, this.y + 2, i, j, 18, 18);
        }

        @Override
        public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
            this.mouseDown = false;
            return super.mouseReleased(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (this.isHovered) {
                this.mouseDown = true;
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public void onPress() {
            this.value = !this.value;
        }

        public boolean getValue() {
            return this.value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}