package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingMode;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelFactory;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelProgressManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneUserLevels extends AbstractScene {

    ScrollableListYio scrollableListYio;
    private ButtonYio backButton;
    private ButtonYio filtersButton;
    private Reaction rbFilters;
    private boolean completedAllowed;
    private boolean historicalAllowed;
    private boolean singlePlayerAllowed;
    private boolean multiplayerAllowed;
    private String searchName;


    public SceneUserLevels(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        scrollableListYio = null;
        initReactions();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(910, Reaction.rbChooseGameModeMenu);

        createList();
        createFiltersButton();

        menuControllerYio.endMenuCreation();
    }


    private void createFiltersButton() {
        filtersButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 912, getString("filters"));
        filtersButton.setReaction(rbFilters);
        filtersButton.setAnimation(Animation.UP);
        filtersButton.disableTouchAnimation();
    }


    private void initReactions() {
        rbFilters = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneUlFilters.create();
            }
        };
    }


    private void createList() {
        if (scrollableListYio == null) {
            initListOnce();
        }

        updateList();
        scrollableListYio.appear();
    }


    private void updateList() {
        scrollableListYio.clearItems();

        Preferences filterPrefs = SceneUlFilters.getFilterPrefs();
        completedAllowed = filterPrefs.getBoolean("completed", true);
        historicalAllowed = filterPrefs.getBoolean("historical", true);
        singlePlayerAllowed = filterPrefs.getBoolean("single_player", true);
        multiplayerAllowed = filterPrefs.getBoolean("multiplayer", true);
        searchName = filterPrefs.getString("search_name", "");

        for (AbstractUserLevel userLevel : UserLevelFactory.getInstance().getLevels()) {
            if (hasToSkipLevel(userLevel)) continue;

            addUserLevelToList(userLevel);
        }

        checkToAddOneLevel();
        checkToCreateAddMapItem();
    }


    private void addUserLevelToList(AbstractUserLevel userLevel) {
        scrollableListYio.addItem(
                userLevel.getKey(),
                getMapTitle(userLevel),
                userLevel.getAuthor()
        );
    }


    private void checkToAddOneLevel() {
        if (scrollableListYio.items.size() > 0) return;

        AbstractUserLevel firstLevel = UserLevelFactory.getInstance().getLevels().get(0);
        addUserLevelToList(firstLevel);
    }


    private boolean hasToSkipLevel(AbstractUserLevel userLevel) {
        if (!completedAllowed) {
            boolean levelCompleted = UserLevelProgressManager.getInstance().isLevelCompleted(userLevel.getKey());
            if (levelCompleted) return true;
        }

        if (!historicalAllowed && userLevel.isHistorical()) return true;
        if (!singlePlayerAllowed && userLevel.isSinglePlayer()) return true;
        if (!multiplayerAllowed && userLevel.isMultiplayer()) return true;

        if (searchName.length() > 0 && !userLevel.getMapName().toLowerCase().contains(searchName.toLowerCase())) return true;

        return false;
    }


    private void checkToCreateAddMapItem() {
        if (!isAddMapItemEnabled()) return;

        scrollableListYio.addItem(
                "add_my_map",
                LanguagesManager.getInstance().getString("add_my_map"),
                " "
        );
    }


    private boolean isAddMapItemEnabled() {
        float numberOfCompletedLevels = UserLevelProgressManager.getInstance().getNumberOfCompletedLevels();
        float allLevels = UserLevelFactory.getInstance().getLevels().size();
        float completionRatio = numberOfCompletedLevels / allLevels;

        return completionRatio > 0.8;
    }


    private String getMapTitle(AbstractUserLevel userLevel) {
        if (UserLevelProgressManager.getInstance().isLevelCompleted(userLevel.getKey())) {
            return "[+] " + userLevel.getMapName();
        }

        return userLevel.getMapName();
    }


    private void initListOnce() {
        scrollableListYio = new ScrollableListYio(menuControllerYio);
        scrollableListYio.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));
        scrollableListYio.setTitle(LanguagesManager.getInstance().getString("user_levels"));
        menuControllerYio.addElementToScene(scrollableListYio);

        scrollableListYio.setListBehavior(new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                OnItemClicked(item);
            }


            @Override
            public void onItemRenamed(ListItemYio item) {

            }


            @Override
            public void onItemDeleteRequested(ListItemYio item) {

            }
        });
    }


    private void OnItemClicked(ListItemYio item) {
        if (item.key.equals("add_my_map")) {
            onAddMyMapClicked();
            return;
        }

        AbstractUserLevel level = UserLevelFactory.getInstance().getLevel(item.key);

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingMode.USER_LEVEL;
        instance.applyFullLevel(level.getFullLevelString());
        instance.colorOffset = level.getColorOffset();
        instance.fogOfWar = level.getFogOfWar();
        instance.diplomacy = level.getDiplomacy();
        instance.ulKey = level.getKey();
        LoadingManager.getInstance().startGame(instance);

        level.onLevelLoaded(menuControllerYio.yioGdxGame.gameController);
    }


    public void onAddMyMapClicked() {
        Scenes.sceneAboutGame.create("how_add_my_map", new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneUserLevels.create();
            }
        }, 913);
    }
}
