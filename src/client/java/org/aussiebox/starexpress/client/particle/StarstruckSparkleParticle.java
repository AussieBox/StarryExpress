package org.aussiebox.starexpress.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StarstruckSparkleParticle extends AnimatedParticle {

    StarstruckSparkleParticle(ClientWorld clientLevel, double d, double e, double f, double g, double h, double i, SpriteProvider spriteSet) {
        super(clientLevel, d, e, f, spriteSet, 0.0125F);
        this.velocityMultiplier = 0.0F;
        this.gravityStrength = 0.0F;
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
        this.scale *= 0.75F;
        this.maxAge = 30 + this.random.nextInt(12);
        this.collidesWithWorld = true;
        this.setTargetColor(Color.WHITE.getRGB());
        this.setSpriteForAge(spriteSet);
    }

    @Override
    public void tick() {
        this.setSprite(this.spriteProvider.getSprite(this.age, this.maxAge));
        if (this.age++ >= this.maxAge)
            this.markDead();
    }

    @Override
    public void move(double d, double e, double f) {
        this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
        this.repositionFromBoundingBox();
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Provider(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
            return new StarstruckSparkleParticle(clientLevel, d, e, f, g, h, i, this.sprites);
        }
    }
}