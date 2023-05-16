const prod = {
  url: {
    API_BASE_URL: 'https://cf2ecs.kmhak.com/',
  }
}

const dev = {
  url: {
    API_BASE_URL: 'http://localhost:8080'
  }
}

export const config = process.env.NODE_ENV === 'development' ? dev : prod
