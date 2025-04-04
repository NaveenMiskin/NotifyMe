from fastapi import FastAPI, WebSocket
from fastapi.responses import HTMLResponse
import asyncio

app = FastAPI()
clients = {}

@app.websocket("/ws/{client_id}")
async def websocket_endpoint(websocket: WebSocket, client_id: str):
    await websocket.accept()
    clients[client_id] = websocket
    try:
        while True:
            data = await websocket.receive_text()
            target_id = "windows" if client_id == "android" else "android"
            if target_id in clients:
                await clients[target_id].send_text(data)
    except:
        del clients[client_id]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
