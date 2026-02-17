# Multi-stage build for cross-platform support (ARM and X86)
FROM --platform=$BUILDPLATFORM node:18-alpine AS builder

WORKDIR /app

# Configure npm mirror (Taobao/npmmirror) for faster downloads in China
RUN npm config set registry https://registry.npmmirror.com

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage - using nginx alpine for cross-platform support
FROM nginx:alpine

# Set timezone
ENV TZ=Asia/Shanghai

# Copy built files
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
