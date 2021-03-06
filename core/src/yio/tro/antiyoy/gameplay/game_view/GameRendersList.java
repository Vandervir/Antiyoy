package yio.tro.antiyoy.gameplay.game_view;

import java.util.ArrayList;

public class GameRendersList {


    GameView gameView;
    public ArrayList<GameRender> list;

    public RenderLevelEditorStuff renderLevelEditorStuff;
    public RenderFogOfWar renderFogOfWar;
    public RenderCityNames renderCityNames;
    public RenderMoveZone renderMoveZone;
    public RenderBackgroundCache renderBackgroundCache;
    public RenderMoney renderMoney;
    public RenderDefenseTips renderDefenseTips;
    public RenderBlackout renderBlackout;
    public RenderSelectedUnit renderSelectedUnit;
    public RenderForefinger renderForefinger;
    public RenderTip renderTip;
    public RenderSelectedHexes renderSelectedHexes;
    public RenderExclamationMarks renderExclamationMarks;
    public RenderResponseAnimHex renderResponseAnimHex;
    public RenderAnimHexes renderAnimHexes;
    public RenderUnits renderUnits;
    public RenderSolidObjects renderSolidObjects;
    public RenderHexLines renderHexLines;
    public RenderDebug renderDebug;
    // init them lower


    public GameRendersList(GameView gameView) {
        this.gameView = gameView;

        list = new ArrayList<>();
    }


    public void create() {
        renderLevelEditorStuff = new RenderLevelEditorStuff(this);
        renderFogOfWar = new RenderFogOfWar(this);
        renderCityNames = new RenderCityNames(this);
        renderMoveZone = new RenderMoveZone(this);
        renderBackgroundCache = new RenderBackgroundCache(this);
        renderMoney = new RenderMoney(this);
        renderDefenseTips = new RenderDefenseTips(this);
        renderBlackout = new RenderBlackout(this);
        renderSelectedUnit = new RenderSelectedUnit(this);
        renderForefinger = new RenderForefinger(this);
        renderTip = new RenderTip(this);
        renderSelectedHexes = new RenderSelectedHexes(this);
        renderExclamationMarks = new RenderExclamationMarks(this);
        renderResponseAnimHex = new RenderResponseAnimHex(this);
        renderAnimHexes = new RenderAnimHexes(this);
        renderUnits = new RenderUnits(this);
        renderSolidObjects = new RenderSolidObjects(this);
        renderHexLines = new RenderHexLines(this);
        renderDebug = new RenderDebug(this);
    }


    public void render() {
        renderLevelEditorStuff.render();
        renderFogOfWar.render();
    }


    public void loadTextures() {
        for (GameRender gameRender : list) {
            gameRender.loadTextures();
        }
    }


    public void disposeTextures() {
        for (GameRender gameRender : list) {
            gameRender.disposeTextures();
        }
    }
}
