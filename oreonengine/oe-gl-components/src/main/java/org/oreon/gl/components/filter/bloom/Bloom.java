package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;

import lombok.Getter;

public class Bloom {

	@Getter
	private GLTexture bloomBlurSceneTexture;
	
	private GLTexture bloomTexture;
	private GLTexture horizontalBloomBlurTexture_div2;
	private GLTexture verticalBloomBlurTexture_div2;
	private GLTexture horizontalBloomBlurTexture_div4;
	private GLTexture verticalBloomBlurTexture_div4;
	private GLTexture horizontalBloomBlurTexture_div8;
	private GLTexture verticalBloomBlurTexture_div8;
	private GLTexture horizontalBloomBlurTexture_div16;
	private GLTexture verticalBloomBlurTexture_div16;
	private GLTexture additiveBlendBloomTexture;
	
	private BloomShader bloomShader;
	private HorizontalBloomBlurShader horizontalBlurShader;
	private VerticalBloomBlurShader verticalBlurShader;
	private BloomBlurSceneShader bloomBlurShader;
	private BloomBlurAdditiveBlendShader bloomBlurAdditiveBlendShader;
	
	public Bloom(){
		
		bloomShader = BloomShader.getInstance();
		bloomBlurAdditiveBlendShader = BloomBlurAdditiveBlendShader.getInstance();
		horizontalBlurShader = HorizontalBloomBlurShader.getInstance();
		verticalBlurShader = VerticalBloomBlurShader.getInstance();
		bloomBlurShader = BloomBlurSceneShader.getInstance();
		
		bloomTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight());
		bloomTexture.bind();
		bloomTexture.clampToEdge();
		bloomTexture.unbind();
		
		additiveBlendBloomTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight());
		additiveBlendBloomTexture.bind();
		additiveBlendBloomTexture.clampToEdge();
		additiveBlendBloomTexture.unbind();
		
		bloomBlurSceneTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(),
						EngineContext.getWindow().getHeight());
		bloomBlurSceneTexture.bind();
		bloomBlurSceneTexture.clampToEdge();
		bloomBlurSceneTexture.unbind();
		
		horizontalBloomBlurTexture_div2 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/2,
				EngineContext.getWindow().getHeight()/2);
		horizontalBloomBlurTexture_div2.bind();
		horizontalBloomBlurTexture_div2.clampToEdge();
		horizontalBloomBlurTexture_div2.unbind();
		
		verticalBloomBlurTexture_div2 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/2,
				EngineContext.getWindow().getHeight()/2);
		verticalBloomBlurTexture_div2.bind();
		verticalBloomBlurTexture_div2.clampToEdge();
		verticalBloomBlurTexture_div2.unbind();
		
		horizontalBloomBlurTexture_div4 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/4,
				EngineContext.getWindow().getHeight()/4);
		horizontalBloomBlurTexture_div4.bind();
		horizontalBloomBlurTexture_div4.clampToEdge();
		horizontalBloomBlurTexture_div4.unbind();
		
		verticalBloomBlurTexture_div4 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/4,
				EngineContext.getWindow().getHeight()/4);
		verticalBloomBlurTexture_div4.bind();
		verticalBloomBlurTexture_div4.clampToEdge();
		verticalBloomBlurTexture_div4.unbind();
		
		horizontalBloomBlurTexture_div8 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/8,
				EngineContext.getWindow().getHeight()/8);
		horizontalBloomBlurTexture_div8.bind();
		horizontalBloomBlurTexture_div8.clampToEdge();
		horizontalBloomBlurTexture_div8.unbind();
		
		verticalBloomBlurTexture_div8 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/8,
				EngineContext.getWindow().getHeight()/8);
		verticalBloomBlurTexture_div8.bind();
		verticalBloomBlurTexture_div8.clampToEdge();
		verticalBloomBlurTexture_div8.unbind();
		
		horizontalBloomBlurTexture_div16 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/16,
				EngineContext.getWindow().getHeight()/16);
		horizontalBloomBlurTexture_div16.bind();
		horizontalBloomBlurTexture_div16.clampToEdge();
		horizontalBloomBlurTexture_div16.unbind();
		
		verticalBloomBlurTexture_div16 = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth()/16,
				EngineContext.getWindow().getHeight()/16);
		verticalBloomBlurTexture_div16.bind();
		verticalBloomBlurTexture_div16.clampToEdge();
		verticalBloomBlurTexture_div16.unbind();
	}
	
	public void render(GLTexture sceneSampler) {
				
		bloomShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, bloomTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, EngineContext.getWindow().getWidth()/2, EngineContext.getWindow().getHeight()/2);
		glDispatchCompute(EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div2.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div4.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, EngineContext.getWindow().getWidth()/4, EngineContext.getWindow().getHeight()/4);
		glDispatchCompute(EngineContext.getWindow().getWidth()/32, EngineContext.getWindow().getHeight()/32, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div4.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div4.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/32, EngineContext.getWindow().getHeight()/32, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div8.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8);
		glDispatchCompute(EngineContext.getWindow().getWidth()/64, EngineContext.getWindow().getHeight()/64, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div8.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div8.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/64, EngineContext.getWindow().getHeight()/64, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div16.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16);
		glDispatchCompute(EngineContext.getWindow().getWidth()/128, EngineContext.getWindow().getHeight()/128, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div16.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div16.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/128, EngineContext.getWindow().getHeight()/128, 1);	
		glFinish();
		
		bloomBlurAdditiveBlendShader.bind();
		glBindImageTexture(0, additiveBlendBloomTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		bloomBlurAdditiveBlendShader.updateUniforms(verticalBloomBlurTexture_div2,
													verticalBloomBlurTexture_div4,
													verticalBloomBlurTexture_div8,
													verticalBloomBlurTexture_div16,
													EngineContext.getWindow().getWidth(),
													EngineContext.getWindow().getHeight());
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		bloomBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, additiveBlendBloomTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, bloomBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}
	
}
