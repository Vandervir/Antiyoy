package yio.tro.antiyoy.gameplay.campaign;

import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingMode;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;


public class CampaignLevelFactory {

    public GameController gameController;
    public static final int NORMAL_LEVELS_START = 9;
    public static final int HARD_LEVELS_START = 24;
    public static final int EXPERT_LEVELS_START = 60;
    private final LevelPackOne levelPackOne;
    private final LevelPackTwo levelPackTwo;
    private final LevelPackThree levelPackThree;
    int index;
    private final LevelPackFour levelPackFour;
    private final LevelPackFive levelPackFive;
    private final LevelPackSix levelPackSix;
    private final LevelPackSeven levelPackSeven;
    private final LevelPackEight levelPackEight;


    public CampaignLevelFactory(GameController gameController) {
        this.gameController = gameController;

        levelPackOne = new LevelPackOne(this);
        levelPackTwo = new LevelPackTwo(this);
        levelPackThree = new LevelPackThree(this);
        levelPackFour = new LevelPackFour(this);
        levelPackFive = new LevelPackFive(this);
        levelPackSix = new LevelPackSix(this);
        levelPackSeven = new LevelPackSeven(this);
        levelPackEight = new LevelPackEight(this);
        index = -1;
    }


    public boolean createCampaignLevel(int index) {
        this.index = index;

        // to avoid crash
        Scenes.sceneMoreCampaignOptions.prepare();

        CampaignProgressManager.getInstance().setCurrentLevelIndex(index);
        gameController.getYioGdxGame().setSelectedLevelIndex(index);
        updateRules(); // used for pack two

        if (checkForTutorial()) return true;
        if (CampaignProgressManager.getInstance().isLevelLocked(index)) return false;
        if (levelPackOne.check()) return true;
        if (levelPackTwo.check()) return true;
        if (levelPackThree.check()) return true;
        if (levelPackFour.check()) return true;
        if (levelPackFive.check()) return true;
        if (levelPackSix.check()) return true;
        if (levelPackSeven.check()) return true;
        if (levelPackEight.check()) return true;

        createLevelWithPredictableRandom();

        return true;
    }


    private void updateRules() {
        GameRules.setSlayRules(gameController.yioGdxGame.menuControllerYio.getCheckButtonById(17).isChecked());
    }


    private boolean checkForTutorial() {
        if (index == 0) { // tutorial level
//            GameRules.setSlayRules(false);
            gameController.initTutorial();
//            GameRules.campaignMode = true;
            return true;
        }
        return false;
    }


    private void createLevelWithPredictableRandom() {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingMode.CAMPAIGN_RANDOM;
        instance.levelSize = getLevelSizeByIndex(index);
        instance.playersNumber = 1;
        instance.colorNumber = getColorNumberByIndex(index);
        instance.difficulty = getDifficultyByIndex(index);
        instance.colorOffset = readColorOffsetFromSlider(instance.colorNumber);
        instance.slayRules = GameRules.slayRules;
        instance.campaignLevelIndex = index;
        LoadingManager.getInstance().startGame(instance);

        checkForHelloMessage(index);
    }


    public int readColorOffsetFromSlider(int colorNumber) {
        return gameController.getColorOffsetBySliderIndex(getColorOffsetSlider().getValueIndex(), colorNumber);
    }


    private SliderYio getColorOffsetSlider() {
        return Scenes.sceneMoreCampaignOptions.colorOffsetSlider;
    }


    public void checkForHelloMessage(int index) {
        if (index == 24) { // first hard level
            MenuControllerYio menuControllerYio = gameController.yioGdxGame.menuControllerYio;
            ArrayList<String> text = menuControllerYio.getArrayListFromString(LanguagesManager.getInstance().getString("level_24"));
            text.add(" ");
            text.add(" ");
            Scenes.sceneTutorialTip.createTutorialTip(text);
            return;
        }
    }





    public static int getDifficultyByIndex(int index) {
        if (index <= 8) return Difficulty.EASY;
        if (index <= 23) return Difficulty.NORMAL;
        if (index >= 60) return Difficulty.EXPERT;
        return Difficulty.HARD;
    }


    private int getColorNumberByIndex(int index) {
        if (index <= 4 || index == 20) return 3;
        if (index <= 7) return 4;
        if (index >= 10 && index <= 13) return 4;
        return 5;
    }


    private int getLevelSizeByIndex(int index) {
        if (index == 4 || index == 7) return LevelSize.MEDIUM;
        if (index == 15) return LevelSize.SMALL;
        if (index == 20 || index == 30 || index == 35) return LevelSize.BIG;
        if (index >= 60 && index <= 64) return LevelSize.MEDIUM;
        if (index > 50 && index <= 53) return LevelSize.MEDIUM;

        if (index <= 10) {
            return LevelSize.SMALL;
        } else if (index <= 40) {
            return LevelSize.MEDIUM;
        } else {
            return LevelSize.BIG;
        }
    }


}
