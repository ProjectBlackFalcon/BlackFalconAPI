package com.ankamagames.dofus.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.core.model.BotInfo;
import com.ankamagames.dofus.core.model.Command;
import com.ankamagames.dofus.core.movement.CellData;
import com.ankamagames.dofus.core.movement.CellMovement;
import com.ankamagames.dofus.core.movement.Movement;
import com.ankamagames.dofus.core.network.DofusConnector;
import com.ankamagames.dofus.network.messages.game.context.roleplay.ChangeMapMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.havenbag.EnterHavenBagRequestMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.npc.NpcDialogReplyMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.npc.NpcGenericActionRequestMessage;
import com.ankamagames.dofus.network.messages.game.dialog.LeaveDialogRequestMessage;
import com.ankamagames.dofus.network.messages.game.interactive.InteractiveUseRequestMessage;
import com.ankamagames.dofus.network.messages.game.interactive.zaap.TeleportRequestMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeBidHouseBuyMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeBidHouseSearchMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeBidHouseTypeMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeObjectMoveKamaMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeObjectMoveMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeObjectMovePricedMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeObjectTransfertListFromInvMessage;
import com.ankamagames.dofus.network.messages.game.inventory.exchanges.ExchangeObjectTransfertListToInvMessage;
import com.ankamagames.dofus.network.messages.security.CheckFileMessage;
import com.ankamagames.dofus.network.messages.game.achievement.AchievementRewardRequestMessage;
import com.ankamagames.dofus.network.messages.game.startup.StartupActionsAllAttributionMessage;
import com.ankamagames.dofus.network.messages.game.dare.DareRewardConsumeRequestMessage;

/**
 * Handler external to the game. It will be only actions that the player/client wants.
 */
public class ApiHandler {

    private static final Logger log = Logger.getLogger(ApiHandler.class);

    private DofusConnector connector;
    private ApiServer server;

    public static final String CONNECT = "connect";
    public static final String DISCONNECT = "disconnect";
    public static final String CHANGE_MAP = "change_map";
    public static final String MOVE = "move";
    public static final String USE_INTERACTIVE = "use_interactive";
    public static final String OPEN_NPC = "open_npc";
    public static final String CLOSE_NPC = "close_npc";
    public static final String ANSWER_NPC = "answer_npc";
    public static final String TRAVEL_BY_ZAAP = "travel_by_zaap";
    public static final String ENTER_BAG = "enter_havenbag";
    public static final String EXIT_BAG = "exit_havenbag";
    public static final String INV_TO_STORAGE = "inv_to_storage";
    public static final String STORAGE_TO_INV = "storage_to_inv";
    public static final String INV_TO_STORAGE_LIST = "inv_to_storage_list";
    public static final String STORAGE_TO_INV_LIST = "storage_to_inv_list";
    public static final String MOVE_KAMAS = "move_kamas";
    public static final String CHECK_FILE = "check_file_message";
    public static final String AH_CATEGORY = "auctionh_select_category";
    public static final String AH_ITEM = "auctionh_select_item";
    public static final String AH_BUY = "auctionh_buy_item";
    public static final String AH_SELL = "auctionh_sell_item";
	public static final String ACHIEVEMENT_GET = "achievement_get";

    public static final String STATUS = "status";
    public static final String SERVER_DOWN = "server down";

    public ApiHandler(final ApiServer server) {
        this.server = server;
    }

    public void handleMessage(final Command command) throws Exception {
        switch (command.getCommand()) {
            case CONNECT:
                handleConnectMessage(command.getParameters());
                break;
            case MOVE:
                handleMoveMessage(command.getParameters());
                break;
            case DISCONNECT:
                handleDisconnectMessage(command.getParameters());
                break;
            case CHANGE_MAP:
                handleChangeMapMessage(command.getParameters());
                break;
            case USE_INTERACTIVE:
                handleUseInteractiveMessage(command.getParameters());
                break;
            case OPEN_NPC:
                handleOpenNpcMessage(command.getParameters());
                break;
            case CLOSE_NPC:
                handleCloseNpcMessage(command.getParameters());
                break;
            case ANSWER_NPC:
                handleReplyNpcMessage(command.getParameters());
                break;
            case TRAVEL_BY_ZAAP:
                handleTravelByZaapMessage(command.getParameters());
                break;
            case ENTER_BAG:
            case EXIT_BAG:
                handleHavenBagMessage(command.getParameters());
                break;
            case INV_TO_STORAGE:
                handleInvToStorageMessage(command.getParameters());
                break;
            case STORAGE_TO_INV:
                handleStorageToInvMessage(command.getParameters());
                break;
            case INV_TO_STORAGE_LIST:
                handleListInvToStorageMessage(command.getParameters());
                break;
            case STORAGE_TO_INV_LIST:
                handleListStorageToInvMessage(command.getParameters());
                break;
            case MOVE_KAMAS:
                handleMoveKamasMessage(command.getParameters());
                break;
            case CHECK_FILE:
                handleCheckFileMessage(command.getParameters());
                break;
            case AH_CATEGORY:
                handleSelectCategoryMessage(command.getParameters());
                break;
            case AH_ITEM:
                handleSelectItemMessage(command.getParameters());
                break;
            case AH_BUY:
                handleBuyItemMessage(command.getParameters());
                break;
            case AH_SELL:
                handleSellItemMessage(command.getParameters());
                break;
            case ACHIEVEMENT_GET:
                handleAcceptAchievement(command.getParameters());
                break;
        }
    }

    private void handleConnectMessage(final Map<String, Object> parameters) {
        this.connector = new DofusConnector(server);
        BotInfo botInfo = new BotInfo(
            (String) parameters.get("username"),
            (String) parameters.get("password"),
            (String) parameters.get("name"),
            (int) parameters.get("serverId")
        );
        this.connector.setBotInfo(botInfo);

        Thread connectorThread = new Thread(connector);
        connectorThread.start();
    }

    @SuppressWarnings("unchecked")
    private void handleMoveMessage(final Map<String, Object> parameters) throws Exception {
        com.ankamagames.dofus.core.movement.Map map = new com.ankamagames.dofus.core.movement.Map();
        map.setUsingNewMovementSystem((Boolean) parameters.get("isUsingNewMovementSystem"));

        List<CellData> cells = ((List<List<Object>>) parameters.get("cells"))
            .stream()
            .map(cell ->
                new CellData(
                    (boolean) cell.get(0),
                    (boolean) cell.get(1),
                    (int) cell.get(2),
                    (int) cell.get(3),
                    (boolean) cell.get(4),
                    (int) cell.get(5)
                )
            )
            .collect(Collectors.toList());
        map.setCells(cells);

        Movement movement = new Movement(connector, map);
        int targetCell = (Integer) parameters.get("target_cell");
        CellMovement mov = movement.moveToCell(targetCell);

        if (mov == null) {
            throw new Error(String.format("Cannot move from %s to %s on map %s",
                this.connector.getBotInfo().getCellId(), targetCell, this.connector.getBotInfo().getMapId())
            );
        }

        mov.performMovement();
    }


    private void handleDisconnectMessage(final Map<String, Object> parameters) throws IOException {
        this.connector.getSocket().close();
    }

    private void handleChangeMapMessage(final Map<String, Object> parameters) throws Exception {
        double targetMapId = Double.parseDouble(String.valueOf(parameters.get("target_map_id")));
        ChangeMapMessage changeMapMessage = new ChangeMapMessage(targetMapId, false);
        this.connector.sendToServer(changeMapMessage);
    }

    private void handleUseInteractiveMessage(final Map<String, Object> parameters) throws Exception {
        InteractiveUseRequestMessage message = new InteractiveUseRequestMessage();
        message.setElemId(Integer.parseInt(String.valueOf(parameters.get("element_id"))));
        message.setSkillInstanceUid(Integer.parseInt(String.valueOf(parameters.get("skill_uid"))));
        this.connector.sendToServer(message);
    }

    private void handleOpenNpcMessage(final Map<String, Object> parameters) throws Exception {
        NpcGenericActionRequestMessage message = new NpcGenericActionRequestMessage();
        message.setNpcActionId(Integer.parseInt(String.valueOf(parameters.get("action_id"))));
        message.setNpcId(Integer.parseInt(String.valueOf(parameters.get("npc_id"))));
        message.setNpcMapId(Double.parseDouble(String.valueOf(parameters.get("map_id"))));
        this.connector.sendToServer(message);
    }

    private void handleReplyNpcMessage(final Map<String, Object> parameters) throws Exception {
        NpcDialogReplyMessage message = new NpcDialogReplyMessage();
        message.setReplyId(Integer.parseInt(String.valueOf(parameters.get("reply_id"))));
        this.connector.sendToServer(message);
    }

    private void handleCloseNpcMessage(final Map<String, Object> parameters) throws Exception {
        this.connector.sendToServer(new LeaveDialogRequestMessage());
    }

    private void handleTravelByZaapMessage(final Map<String, Object> parameters) throws Exception {
        TeleportRequestMessage message = new TeleportRequestMessage();
        message.setDestinationType(0);
        message.setSourceType(0);
        message.setMapId(Double.parseDouble(String.valueOf(parameters.get("target_map_id"))));
        this.connector.sendToServer(message);
    }

    private void handleHavenBagMessage(final Map<String, Object> parameters) throws Exception {
        EnterHavenBagRequestMessage message = new EnterHavenBagRequestMessage();
        message.setHavenBagOwner((long) this.connector.getBotInfo().getId());
        this.connector.sendToServer(message);
    }

    private void handleInvToStorageMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeObjectMoveMessage message = new ExchangeObjectMoveMessage();
        message.setObjectUID(Integer.parseInt(String.valueOf(parameters.get("item_uid"))));
        message.setQuantity(Integer.parseInt(String.valueOf(parameters.get("quantity"))));
        this.connector.sendToServer(message);
    }

    private void handleStorageToInvMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeObjectMoveMessage message = new ExchangeObjectMoveMessage();
        message.setObjectUID(Integer.parseInt(String.valueOf(parameters.get("item_uid"))));
        message.setQuantity(-Integer.parseInt(String.valueOf(parameters.get("quantity"))));
        this.connector.sendToServer(message);
    }

    private void handleListInvToStorageMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeObjectTransfertListFromInvMessage message = new ExchangeObjectTransfertListFromInvMessage();
        message.setIds((List<Integer>) parameters.get("items_uids"));
        this.connector.sendToServer(message);
    }

    private void handleListStorageToInvMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeObjectTransfertListToInvMessage message = new ExchangeObjectTransfertListToInvMessage();
        message.setIds((List<Integer>) parameters.get("items_uids"));
        this.connector.sendToServer(message);
    }

    private void handleMoveKamasMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeObjectMoveKamaMessage message = new ExchangeObjectMoveKamaMessage();
        message.setQuantity(Long.parseLong(String.valueOf(parameters.get("quantity"))));
        this.connector.sendToServer(message);
    }

    private void handleCheckFileMessage(final Map<String, Object> parameters) throws Exception {
        CheckFileMessage message = new CheckFileMessage();
        message.setType(Integer.parseInt(String.valueOf(parameters.get("type"))));
        message.setValue(String.valueOf(parameters.get("value")));
        message.setFilenameHash(String.valueOf(parameters.get("filenameHash")));
        this.connector.sendToServer(message);
    }

    private void handleSelectCategoryMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeBidHouseTypeMessage message = new ExchangeBidHouseTypeMessage();

        if (this.connector.getBotInfo().getAuctionHouseCategory() != 0) {
            message.setType(this.connector.getBotInfo().getAuctionHouseCategory());
            message.setFollow(false);
            this.connector.sendToServer(message);
        }

        int newType = Integer.parseInt(String.valueOf(parameters.get("category_id")));
        this.connector.getBotInfo().setAuctionHouseCategory(newType);
        message.setType(newType);
        message.setFollow(true);
        this.connector.sendToServer(message);
    }

    private void handleSelectItemMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeBidHouseSearchMessage message = new ExchangeBidHouseSearchMessage();

        if (this.connector.getBotInfo().getAuctionHouseItem() != 0) {
            message.setGenId(this.connector.getBotInfo().getAuctionHouseItem());
            message.setFollow(false);
            this.connector.sendToServer(message);
        }

        int newType = Integer.parseInt(String.valueOf(parameters.get("general_id")));
        this.connector.getBotInfo().setAuctionHouseItem(newType);
        message.setGenId(newType);
        message.setFollow(true);
        this.connector.sendToServer(message);
    }

    private void handleBuyItemMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeBidHouseBuyMessage message = new ExchangeBidHouseBuyMessage();
        message.setUid(Integer.parseInt(String.valueOf(parameters.get("unique_id"))));
        message.setQty(Integer.parseInt(String.valueOf(parameters.get("quantity"))));
        message.setPrice(Integer.parseInt(String.valueOf(parameters.get("price"))));
        this.connector.sendToServer(message);
    }

    private void handleSellItemMessage(final Map<String, Object> parameters) throws Exception {
        ExchangeObjectMovePricedMessage message = new ExchangeObjectMovePricedMessage();
        message.setObjectUID(Integer.parseInt(String.valueOf(parameters.get("unique_id"))));
        message.setQuantity(Integer.parseInt(String.valueOf(parameters.get("quantity"))));
        message.setPrice(Integer.parseInt(String.valueOf(parameters.get("price"))));
        this.connector.sendToServer(message);
    }
	
    private void handleAcceptAchievement(final Map<String, Object> parameters) throws Exception {
        AchievementRewardRequestMessage message_1 = new AchievementRewardRequestMessage();
        message_1.setAchievementId(-1);
        this.connector.sendToServer(message_1);
		
        StartupActionsAllAttributionMessage message_2 = new StartupActionsAllAttributionMessage();
        message_2.setCharacterId(Integer.parseInt(String.valueOf(parameters.get("actor_id"))));
        this.connector.sendToServer(message_2);
		
        DareRewardConsumeRequestMessage message_3 = new DareRewardConsumeRequestMessage();
        message_3.setDareId(-1);
        message_3.setType(0);
        this.connector.sendToServer(message_3);
    }
}
