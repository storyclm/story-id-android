openapi-generator generate -i https://staging-id.storychannels.app/swagger/v1/swagger.json -g java --api-package=ru.breffi.storyid.generated_api.api --model-package=ru.breffi.storyid.generated_api.model -o tmp_gen --library=retrofit2 -p dateLibrary=joda -p hideGenerationTimestamp=true --skip-validate-spec
cp -R tmp_gen/src/main/java src/main
rm -d -R tmp_gen