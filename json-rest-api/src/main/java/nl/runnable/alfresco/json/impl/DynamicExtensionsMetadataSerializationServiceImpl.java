package nl.runnable.alfresco.json.impl;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.json.DynamicExtensionsMetadataSerializationService;
import nl.runnable.alfresco.metadata.ExtensionMetadata;
import nl.runnable.alfresco.metadata.ModelMetadata;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

@ManagedBean
public class DynamicExtensionsMetadataSerializationServiceImpl implements DynamicExtensionsMetadataSerializationService {

	/* Main operations */

	public void serializeDynamicExtensionsMetadata(final Collection<ExtensionMetadata> metadatas, final JsonGenerator generator) throws IOException {
		generator.writeStartObject();
		generator.writeArrayFieldStart("dynamicExtensions");
		for (final ExtensionMetadata metadata : metadatas) {
			serializeMetadata(generator, metadata);
		}
		generator.writeEndArray();
		generator.writeEndObject();
	}

	private void serializeMetadata(final JsonGenerator generator, final ExtensionMetadata metadata) throws IOException,
			JsonGenerationException {
		generator.writeStartObject();
		generator.writeStringField("name", metadata.getName());
		generator.writeStringField("version", metadata.getVersion());
		generator.writeArrayFieldStart("models");
		for (final ModelMetadata model : metadata.getModels()) {
			serializeModel(generator, model);
		}
		generator.writeEndArray();
		generator.writeEndObject();
	}

	/* Utility operations */

	protected void serializeModel(final JsonGenerator generator, final ModelMetadata model) throws IOException,
			JsonGenerationException {
		generator.writeStartObject();
		generator.writeStringField("name", model.getName());
		generator.writeStringField("description", model.getDescription());
		generator.writeEndObject();
	}
}