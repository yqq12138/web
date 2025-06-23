<template>
  <div class="container">
    <div class="chat-box">
      <h1>AI图片对话系统</h1>

      <div class="upload-section">
        <input type="file" accept="image/*" @change="handleImageUpload">
        <p>（请上传图片进行AI识别）</p>
      </div>

      <div class="chat-history" ref="chatHistory">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']">
          <template v-if="msg.role === 'user'">
            <span class="prefix">你：</span>{{ extractText(msg.content) }}
          </template>
          <template v-else>
            <template v-if="typeof msg.content === 'object' && msg.content.analysis">
              <div class="analysis-text">分析结果: {{ msg.content.analysis }}</div>
              <div class="coordinates-text">
                异常坐标: ({{ msg.content.coordinates.x1 }}, {{ msg.content.coordinates.y1 }}) 至 ({{ msg.content.coordinates.x2 }}, {{ msg.content.coordinates.y2 }})
              </div>
              <div class="image-container">
                <img v-if="msg.content.marked_image" :src="msg.content.marked_image" alt="标注图" class="annotated-image" />
                <img v-else-if="msg.content.original_image" :src="msg.content.original_image" alt="原图" class="original-image" />
              </div>
            </template>
            <template v-else>
              <span class="prefix">助手：</span>{{ extractText(msg.content) }}
            </template>
          </template>
        </div>
      </div>
      <div v-if="loading" class="loading-animation">
        <p>AI识别中，请稍候...</p>
        <div class="spinner"></div>
      </div>
    </div>
    <div class="background-decor"></div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      messages: [],
      loading: false
    };
  },
  methods: {
    handleImageUpload(event) {
      const file = event.target.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        const base64Data = e.target.result.split(',')[1];
        const mimeType = file.type;

        // 构造仅用于发送的 message，不 push 给界面显示
        const imageMessage = {
          role: "user",
          content: [
            {
              type: "text",
              text: "请分析这张图片中的工件是否存在异常。如果存在异常，请以JSON格式返回分析结果，包括异常描述和异常部位的坐标（格式：{\"x1\": 左上角x, \"y1\": 左上角y, \"x2\": 右下角x, \"y2\": 右下角y}）。例如：{\"analysis\": \"发现裂纹\",\"coordinates\": {\"x1\": 100, \"y1\": 200, \"x2\": 150, \"y2\": 250}}"
            },
            {
              type: "image_url",
              image_url: {
                url: `data:${mimeType};base64,${base64Data}`
              }
            }
          ]
        };

        // 可选：添加“你上传了一张图片”提示
        this.messages.push({
          role: "user",
          content: [
            {
              type: "text",
              text: "[图片已上传]"
            }
          ]
        });

        this.sendToAI([imageMessage]);
      };

      reader.readAsDataURL(file);
    },

    sendToAI(newMessages) {
      this.loading = true;
      fetch('/api/detection/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ messages: newMessages })
      })
          .then(res => res.json())
          .then(data => {
            if (data.success) {
              const responseMessages = data.messages.map(msg => {
                if (msg.role === 'assistant' && typeof msg.content === 'string') {
                  try {
                    msg.content = JSON.parse(msg.content);
                  } catch {
                    msg.content = { analysis: msg.content };
                  }
                }
                return msg;
              });

              this.messages.push(...responseMessages);

              this.$nextTick(() => {
                const el = this.$refs.chatHistory;
                if (el) el.scrollTop = el.scrollHeight;
              });
            } else {
              alert("错误: " + data.error);
            }
          })
          .catch(err => alert("网络错误: " + err))
          .finally(() => {
            this.loading = false;
          });
    },
    extractText(content) {
      if (Array.isArray(content)) {
        return content.map(c => c.type === 'text' ? c.text : '').join(' ').trim();
      } else if (typeof content === 'string') {
        return content;
      }
      return '';
    }
  }
};
</script>

<style scoped>
.container {
  position: absolute;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, #1c1c1c, #2a2a2a);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 20px;
  overflow: auto;
  box-sizing: border-box;
}

.chat-box {
  width: 100%;
  max-width: 700px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid #00eaff55;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 234, 255, 0.1);
  padding: 30px;
  box-sizing: border-box;
}

.chat-box h1 {
  font-size: 24px;
  color: #00eaff;
  margin-bottom: 20px;
  text-align: center;
}

.upload-section {
  margin-bottom: 20px;
  text-align: center;
  color: #aaa;
}

.chat-history {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.03);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #00eaff22;
  box-shadow: inset 0 0 10px rgba(0, 234, 255, 0.05);
}

.message {
  margin-bottom: 16px;
  padding: 12px 16px;
  border-radius: 10px;
  max-width: 80%;
  word-break: break-word;
  line-height: 1.6;
  font-size: 15px;
}

.user-message {
  background: #003344;
  color: #bbdefb;
  margin-left: auto;
  text-align: right;
}

.ai-message {
  background: #00261f;
  color: #c8e6c9;
  margin-right: auto;
  text-align: left;
}

.prefix {
  font-weight: bold;
  margin-right: 6px;
}

.analysis-text {
  font-weight: bold;
  margin-bottom: 8px;
  color: #00ffc3;
}

.coordinates-text {
  color: #aaf0ff;
  font-size: 14px;
  margin-bottom: 10px;
}

.image-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.original-image,
.annotated-image {
  max-width: 200px;
  border-radius: 8px;
  border: 1px solid #00eaff33;
  box-shadow: 0 0 8px rgba(0, 234, 255, 0.2);
}

.input-area {
  display: flex;
  gap: 10px;
  width: 100%;
}

.input-area input[type="text"] {
  flex: 1;
  padding: 10px;
  border: 1px solid #00eaff55;
  border-radius: 8px;
  background: #222;
  color: #fff;
  outline: none;
}

.input-area button {
  padding: 10px 20px;
  background: #00eaff;
  border: none;
  border-radius: 8px;
  color: #000;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
}

.input-area button:hover {
  background: #00bcd4;
}

input[type="file"] {
  margin: 10px 0;
  color: #fff;
}

.background-decor {
  position: absolute;
  bottom: -100px;
  left: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle at center, rgba(0, 234, 255, 0.2), transparent);
  filter: blur(120px);
  pointer-events: none;
}

.loading-animation {
  text-align: center;
  color: #fff;
  margin-top: 1rem;
}

.spinner {
  margin: 10px auto;
  width: 30px;
  height: 30px;
  border: 4px solid #999;
  border-top-color: #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

</style>
