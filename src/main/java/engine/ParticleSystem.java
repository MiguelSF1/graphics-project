package engine;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleSystem {
    private List<Particle> aliveParticles;
    private float pps;
    private float speed;
    private float life;

    public ParticleSystem(float pps, float speed, float life) {
        this.pps = pps;
        this.speed = speed;
        this.life = life;
        this.aliveParticles = new ArrayList<>();
    }

    public List<Particle> getAliveParticles() {
        return aliveParticles;
    }

    public void generateParticles(Vector3f center, float dt){
        float particlesToCreate = pps * dt;
        int count = (int) Math.floor(particlesToCreate);
        for (int i = 0; i < count; i++){
            emitParticle(center);
        }
    }

    private void emitParticle(Vector3f center){
        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
        velocity.normalize();
        velocity.mul(speed);
        this.aliveParticles.add(new Particle(new Vector3f(center), velocity, life, 0, 1, 1));
    }

    public void checkParticleLifespan(float dt) {
        Iterator<Particle> iterator = aliveParticles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            boolean stillAlive = particle.update(dt);
            if (!stillAlive) {
                iterator.remove();
            }
        }
    }
}
