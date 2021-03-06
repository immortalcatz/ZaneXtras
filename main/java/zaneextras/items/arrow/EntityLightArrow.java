package zaneextras.items.arrow;

import java.util.List;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityLightArrow extends EntityArrow
		implements IEntityAdditionalSpawnData, IThrowableEntity {
	private int field_145791_d = -1;
	private int field_145792_e = -1;
	private int field_145789_f = -1;
	private Block field_145790_g;
	private int inData;
	private boolean inGround;
	/** 1 if the player can pick up the arrow */
	public int canBePickedUp;
	/** Seems to be some sort of timer for animating an arrow. */
	public int arrowShake;
	/** The owner of this arrow. */
	public Entity shootingEntity;
	private int ticksInGround;
	private int ticksInAir;
	private double damage = 5.0D;
	/** The amount of knockback an arrow applies when it hits a mob. */
	private int knockbackStrength = 1;
	private static final String __OBFID = "CL_00001715";
	
	public EntityLightArrow(World p_i1753_1_) {
		super(p_i1753_1_);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
	}
	
	public EntityLightArrow(World p_i1754_1_, double p_i1754_2_,
			double p_i1754_4_, double p_i1754_6_) {
		super(p_i1754_1_);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
		this.setPosition(p_i1754_2_, p_i1754_4_, p_i1754_6_);
		this.yOffset = 0.0F;
	}
	
	public EntityLightArrow(World p_i1755_1_, EntityLivingBase p_i1755_2_,
			EntityLivingBase p_i1755_3_, float p_i1755_4_, float p_i1755_5_) {
		super(p_i1755_1_);
		this.renderDistanceWeight = 10.0D;
		this.shootingEntity = p_i1755_2_;
		
		if (p_i1755_2_ instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}
		
		this.posY = p_i1755_2_.posY + p_i1755_2_.getEyeHeight()
				- 0.10000000149011612D;
		double d0 = p_i1755_3_.posX - p_i1755_2_.posX;
		double d1 = p_i1755_3_.boundingBox.minY + p_i1755_3_.height / 3.0F
				- this.posY;
		double d2 = p_i1755_3_.posZ - p_i1755_2_.posZ;
		double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		
		if (d3 >= 1.0E-7D) {
			float f2 = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
			double d4 = d0 / d3;
			double d5 = d2 / d3;
			this.setLocationAndAngles(p_i1755_2_.posX + d4, this.posY,
					p_i1755_2_.posZ + d5, f2, f3);
			this.yOffset = 0.0F;
			float f4 = (float) d3 * 0.2F;
			this.setThrowableHeading(d0, d1 + f4, d2, p_i1755_4_, p_i1755_5_);
		}
	}
	
	public EntityLightArrow(World p_i1756_1_, EntityLivingBase p_i1756_2_,
			float p_i1756_3_) {
		super(p_i1756_1_);
		this.renderDistanceWeight = 10.0D;
		this.shootingEntity = p_i1756_2_;
		
		if (p_i1756_2_ instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}
		
		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(p_i1756_2_.posX,
				p_i1756_2_.posY + p_i1756_2_.getEyeHeight(), p_i1756_2_.posZ,
				p_i1756_2_.rotationYaw, p_i1756_2_.rotationPitch);
		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
				* 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
				* 0.16F;
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = -MathHelper
				.sin(this.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.motionZ = MathHelper
				.cos(this.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.motionY = (-MathHelper
				.sin(this.rotationPitch / 180.0F * (float) Math.PI));
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ,
				p_i1756_3_ * 1.5F, 1.0F);
	}
	
	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onEntityUpdate();
		
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(
					this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math
					.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math
					.atan2(this.motionY, f) * 180.0D / Math.PI);
		}
		
		Block block = this.worldObj.getBlock(this.field_145791_d,
				this.field_145792_e, this.field_145789_f);
		
		if (block.getMaterial() != Material.air) {
			block.setBlockBoundsBasedOnState(this.worldObj, this.field_145791_d,
					this.field_145792_e, this.field_145789_f);
			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(
					this.worldObj, this.field_145791_d, this.field_145792_e,
					this.field_145789_f);
			
			if (axisalignedbb != null && axisalignedbb.isVecInside(
					Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}
		
		if (this.arrowShake > 0) {
			--this.arrowShake;
		}
		
		if (this.inGround) {
			int j = this.worldObj.getBlockMetadata(this.field_145791_d,
					this.field_145792_e, this.field_145789_f);
			
			if (block == this.field_145790_g && j == this.inData) {
				++this.ticksInGround;
				
				if (this.ticksInGround == 1200) {
					this.setDead();
				}
			} else {
				this.inGround = false;
				this.motionX *= this.rand.nextFloat() * 0.2F;
				this.motionY *= this.rand.nextFloat() * 0.2F;
				this.motionZ *= this.rand.nextFloat() * 0.2F;
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY,
					this.posZ);
			Vec3 vec3 = Vec3.createVectorHelper(this.posX + this.motionX,
					this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj
					.func_147447_a(vec31, vec3, false, true, false);
			vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			vec3 = Vec3.createVectorHelper(this.posX + this.motionX,
					this.posY + this.motionY, this.posZ + this.motionZ);
			
			if (movingobjectposition != null) {
				vec3 = Vec3.createVectorHelper(
						movingobjectposition.hitVec.xCoord,
						movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord);
			}
			
			Entity entity = null;
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox
							.addCoord(this.motionX, this.motionY, this.motionZ)
							.expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f1;
			
			for (i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);
				
				if (entity1.canBeCollidedWith()
						&& (entity1 != this.shootingEntity
								|| this.ticksInAir >= 5)) {
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox
							.expand(f1, f1, f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1
							.calculateIntercept(vec31, vec3);
					
					if (movingobjectposition1 != null) {
						double d1 = vec31
								.distanceTo(movingobjectposition1.hitVec);
						
						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}
			
			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}
			
			if (movingobjectposition != null
					&& movingobjectposition.entityHit != null
					&& movingobjectposition.entityHit instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;
				
				if (entityplayer.capabilities.disableDamage
						|| this.shootingEntity instanceof EntityPlayer
								&& !((EntityPlayer) this.shootingEntity)
										.canAttackPlayer(entityplayer)) {
					movingobjectposition = null;
				}
			}
			
			float f2;
			float f4;
			
			if (movingobjectposition != null) {
				if (movingobjectposition.entityHit != null) {
					f2 = MathHelper.sqrt_double(this.motionX * this.motionX
							+ this.motionY * this.motionY
							+ this.motionZ * this.motionZ);
					int k = MathHelper.ceiling_double_int(f2 * this.damage);
					
					if (this.getIsCritical()) {
						k += this.rand.nextInt(k / 2 + 2);
					}
					
					DamageSource damagesource = null;
					
					if (this.shootingEntity == null) {
						damagesource = DamageSource.causeArrowDamage(this,
								this);
					} else {
						damagesource = DamageSource.causeArrowDamage(this,
								this.shootingEntity);
					}
					
					if (this.isBurning()
							&& !(movingobjectposition.entityHit instanceof EntityEnderman)) {
						movingobjectposition.entityHit.setFire(5);
					}
					
					if (movingobjectposition.entityHit
							.attackEntityFrom(damagesource, k)) {
						if (movingobjectposition.entityHit instanceof EntityLivingBase) {
							EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;
							
							if (!this.worldObj.isRemote) {
								entitylivingbase.setArrowCountInEntity(
										entitylivingbase.getArrowCountInEntity()
												+ 1);
							}
							
							if (this.knockbackStrength > 0) {
								f4 = MathHelper
										.sqrt_double(this.motionX * this.motionX
												+ this.motionZ * this.motionZ);
								
								if (f4 > 0.0F) {
									movingobjectposition.entityHit.addVelocity(
											this.motionX
													* this.knockbackStrength
													* 0.6000000238418579D / f4,
											0.1D,
											this.motionZ
													* this.knockbackStrength
													* 0.6000000238418579D / f4);
								}
							}
							
							if (this.shootingEntity != null
									&& this.shootingEntity instanceof EntityLivingBase) {
								EnchantmentHelper.func_151384_a(
										entitylivingbase, this.shootingEntity);
								EnchantmentHelper.func_151385_b(
										(EntityLivingBase) this.shootingEntity,
										entitylivingbase);
							}
							
							if (this.shootingEntity != null
									&& movingobjectposition.entityHit != this.shootingEntity
									&& movingobjectposition.entityHit instanceof EntityPlayer
									&& this.shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler
										.sendPacket(
												new S2BPacketChangeGameState(6,
														0.0F));
							}
						}
						
						this.playSound("random.bowhit", 1.0F,
								1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						
						if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
							this.setDead();
						}
					} else {
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.field_145791_d = movingobjectposition.blockX;
					this.field_145792_e = movingobjectposition.blockY;
					this.field_145789_f = movingobjectposition.blockZ;
					this.field_145790_g = this.worldObj.getBlock(
							this.field_145791_d, this.field_145792_e,
							this.field_145789_f);
					this.inData = this.worldObj.getBlockMetadata(
							this.field_145791_d, this.field_145792_e,
							this.field_145789_f);
					this.motionX = ((float) (movingobjectposition.hitVec.xCoord
							- this.posX));
					this.motionY = ((float) (movingobjectposition.hitVec.yCoord
							- this.posY));
					this.motionZ = ((float) (movingobjectposition.hitVec.zCoord
							- this.posZ));
					f2 = MathHelper.sqrt_double(this.motionX * this.motionX
							+ this.motionY * this.motionY
							+ this.motionZ * this.motionZ);
					this.posX -= this.motionX / f2 * 0.05000000074505806D;
					this.posY -= this.motionY / f2 * 0.05000000074505806D;
					this.posZ -= this.motionZ / f2 * 0.05000000074505806D;
					this.playSound("random.bowhit", 1.0F,
							1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;
					this.setIsCritical(false);
					
					if (this.field_145790_g.getMaterial() != Material.air) {
						this.field_145790_g.onEntityCollidedWithBlock(
								this.worldObj, this.field_145791_d,
								this.field_145792_e, this.field_145789_f, this);
					}
				}
			}
			
			if (this.getIsCritical()) {
				for (i = 0; i < 4; ++i) {
					this.worldObj.spawnParticle("crit",
							this.posX + this.motionX * i / 4.0D,
							this.posY + this.motionY * i / 4.0D,
							this.posZ + this.motionZ * i / 4.0D, -this.motionX,
							-this.motionY + 0.2D, -this.motionZ);
				}
			}
			
			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			f2 = MathHelper.sqrt_double(
					this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ)
					* 180.0D / Math.PI);
			
			for (this.rotationPitch = (float) (Math.atan2(this.motionY, f2)
					* 180.0D / Math.PI); this.rotationPitch
							- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
				;
			}
			
			while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
				this.prevRotationPitch += 360.0F;
			}
			
			while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
				this.prevRotationYaw -= 360.0F;
			}
			
			while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
				this.prevRotationYaw += 360.0F;
			}
			
			this.rotationPitch = this.prevRotationPitch
					+ (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw
					+ (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			f1 = 0.05F;
			
			if (this.isInWater()) {
				for (int l = 0; l < 4; ++l) {
					f4 = 0.25F;
					this.worldObj.spawnParticle("bubble",
							this.posX - this.motionX * f4,
							this.posY - this.motionY * f4,
							this.posZ - this.motionZ * f4, this.motionX,
							this.motionY, this.motionZ);
				}
				
				f3 = 0.8F;
			}
			
			if (this.isWet()) {
				this.extinguish();
			}
			
			this.motionX *= f3;
			this.motionY *= f3;
			this.motionZ *= f3;
			this.motionY -= f1;
			this.setPosition(this.posX, this.posY, this.posZ);
			this.func_145775_I();
		}
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(
				shootingEntity != null ? shootingEntity.getEntityId() : -1);
	}
	
	@Override
	public void readSpawnData(ByteBuf buffer) {
		// Replicate EntityArrow's special spawn packet handling from
		// NetHandlerPlayClient#handleSpawnObject:
		Entity shooter = worldObj.getEntityByID(buffer.readInt());
		if (shooter instanceof EntityLivingBase) {
			shootingEntity = shooter;
		}
	}
	
	@Override
	public Entity getThrower() {
		return this.shootingEntity;
	}
	
	@Override
	public void setThrower(Entity entity) {
		this.shootingEntity = entity;
	}
	
}