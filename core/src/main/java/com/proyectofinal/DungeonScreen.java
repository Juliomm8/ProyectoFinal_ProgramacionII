package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Pantalla principal con mundo procedural “infinito”, usando variantes de pasto.
 */
public class DungeonScreen extends PantallaBase {
    private final String playerClass;
    private PlayerActor playerActor;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera cam;

    // Mapa procedural
    private MapaProcedural generator;
    private final int tileSize = 32;
    private Texture[] texPastoV_variants; // 4 variantes de pasto verde
    private Texture texPastoA, texCamino, texHierbaV, texHierbaA;

    // Jugador y pociones
    private Texture texPlayer, texHP, texEXP, texMana, texEscudo, texMunicion;

    public DungeonScreen(String playerClass) {
        super();
        this.playerClass = playerClass;
    }

    @Override
    protected void initUI() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = 0.6f;

        // 1) Crear jugador y actor
        Jugador log = switch(playerClass) {
            case "Arquero" -> new Arquero("Legolas",100,15,0.8f,10);
            case "Mago"    -> new Mago("Gandalf",80,12,50);
            case "Caballero"-> new Caballero("Arthur",120,15);
            default        -> new Jugador("Héroe",100,10);
        };
        String lower = playerClass.toLowerCase();
        texPlayer = new Texture(Gdx.files.internal("PersonajesPrincipales/"+playerClass+"/"+lower+".png"));
        playerActor = new PlayerActor(log, texPlayer);
        float spawnPx = 50f, spawnPy = 50f;
        playerActor.setPosition(spawnPx,spawnPy);
        stage.addActor(playerActor);

        // 2) Inicializar mapa procedural
        int w = 200, h = 150;
        long seed = System.currentTimeMillis();
        int sx = (int)(spawnPx/tileSize), sy = (int)(spawnPy/tileSize);
        generator = new MapaProcedural(w,h,seed,sx,sy);

        // 3) Cargar texturas de tiles
        texPastoV_variants = new Texture[]{
            new Texture(Gdx.files.internal("Mapa/Pasto/pastoVerde_0.png")),
            new Texture(Gdx.files.internal("Mapa/Pasto/pastoVerde_1.png")),
            new Texture(Gdx.files.internal("Mapa/Pasto/pastoVerde_2.png")),
            new Texture(Gdx.files.internal("Mapa/Pasto/pastoVerde_3.png"))
        };
        texPastoA  = new Texture(Gdx.files.internal("Mapa/Pasto/pastoAmarillo.png"));
        texCamino  = new Texture(Gdx.files.internal("Mapa/Piedras/piedras.png"));
        texHierbaV = new Texture(Gdx.files.internal("Mapa/Pasto/hiervaVerde.png"));
        texHierbaA = new Texture(Gdx.files.internal("Mapa/Pasto/hiervaAmarilla.png"));

        // 4) Cargar y colocar pociones
        texHP  = new Texture(Gdx.files.internal("Pociones/pocionHP.png"));
        texEXP = new Texture(Gdx.files.internal("Pociones/pocionXP.png"));
        crearPocionActor(new PocionHP("Poción Vida",30), texHP, 100f, 150f);
        crearPocionActor(new PocionEXP("Poción EXP",1), texEXP, 200f, 150f);
        if (playerClass.equals("Arquero")) {
            texMunicion = new Texture(Gdx.files.internal("Pociones/pocionMunicion.png"));
            crearPocionActor(new PocionFlechas("Poción Munición",5), texMunicion,300f,150f);
        } else if (playerClass.equals("Mago")) {
            texMana = new Texture(Gdx.files.internal("Pociones/pocionMana.png"));
            crearPocionActor(new PocionMana("Poción Maná",20), texMana,300f,150f);
        } else {
            texEscudo = new Texture(Gdx.files.internal("Pociones/pocionEscudo.png"));
            crearPocionActor(new PocionMana("Poción Escudo",20), texEscudo,300f,150f);
        }
    }

    private void crearPocionActor(Pocion pocion, Texture tex, float x, float y) {
        PocionActor actor = new PocionActor(pocion, tex);
        actor.setPosition(x, y);
        stage.addActor(actor);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        manejarEntrada(delta);
        cam.position.set(
            playerActor.getX()+playerActor.getWidth()/2,
            playerActor.getY()+playerActor.getHeight()/2,0);
        cam.update(); batch.setProjectionMatrix(cam.combined);

        int minX=(int)((cam.position.x-cam.viewportWidth/2)/tileSize)-1;
        int maxX=(int)((cam.position.x+cam.viewportWidth/2)/tileSize)+1;
        int minY=(int)((cam.position.y-cam.viewportHeight/2)/tileSize)-1;
        int maxY=(int)((cam.position.y+cam.viewportHeight/2)/tileSize)+1;

        batch.begin();
        for(int y=minY; y<=maxY; y++) {
            for(int x=minX; x<=maxX; x++) {
                MapaProcedural.Tile t = generator.getTile(x,y);
                Texture base;
                if (t==MapaProcedural.Tile.CAMINO) base=texCamino;
                else if (t==MapaProcedural.Tile.PASTO_AMARILLO) base=texPastoA;
                else {
                    int idx = (x & 1) + ((y & 1) << 1);
                    base = texPastoV_variants[idx];
                }
                batch.draw(base,x*tileSize,y*tileSize,tileSize,tileSize);
                if(t==MapaProcedural.Tile.PASTO_VERDE && generator.hasOverlayVerde(x,y))
                    batch.draw(texHierbaV,x*tileSize,y*tileSize,tileSize,tileSize);
                if(t==MapaProcedural.Tile.PASTO_AMARILLO && generator.hasOverlayAmarillo(x,y))
                    batch.draw(texHierbaA,x*tileSize,y*tileSize,tileSize,tileSize);
            }
        }
        batch.end();

        stage.act(delta); stage.draw();
        Rectangle b = playerActor.getBounds();
        for (Actor a:stage.getActors()) {
            if (a instanceof PocionActor pa && pa.getBounds().overlaps(b)) {
                playerActor.getJugador().recogerPocion(pa.getPocion()); pa.remove();
            }
        }
        batch.begin(); playerActor.dibujarHUD(batch,font);
        if (playerActor.getJugador() instanceof Arquero ar) ar.actualizar(delta);
        batch.end();
    }

    private void manejarEntrada(float d) {
        float v=200f; Vector2 m=new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) m.y+=1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) m.y-=1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) m.x-=1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) m.x+=1;
        if (m.len2()>0) m.nor().scl(v*d);
        playerActor.moveBy(m.x,m.y);
    }

    @Override
    public void dispose() {
        super.dispose(); batch.dispose(); font.dispose();
        texPlayer.dispose(); texHP.dispose(); texEXP.dispose();
        if (texMana!=null) texMana.dispose(); if (texEscudo!=null) texEscudo.dispose();
        if (texMunicion!=null) texMunicion.dispose();
        for (Texture t: texPastoV_variants) t.dispose();
        texPastoA.dispose(); texCamino.dispose(); texHierbaV.dispose(); texHierbaA.dispose();
    }
}
