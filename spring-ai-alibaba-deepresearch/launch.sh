sudo docker run -d \
  --name alibaba-deepresearch \
  -e AI_DASHSCOPE_API_KEY="sk-7de471a4993f4c98af7fa25271b3347a" \
  -e TAVILY_API_KEY="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" \
  -p 8080:8080 \
  alibaba-deepresearch:v1.0
