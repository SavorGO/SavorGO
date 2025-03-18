# Use official Node.js image as base
FROM node:20-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock / pnpm-lock.yaml)
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Build the Next.js app
RUN npm run build

# Use a lightweight Node.js image for production
FROM node:20-alpine AS runner

# Set working directory
WORKDIR /app

# Copy built files from the builder stage
COPY --from=builder /app .

# Expose port 3000
EXPOSE 3000

# Start the application
CMD ["npm", "run", "start"]
