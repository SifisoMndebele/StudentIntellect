from dashboard.ui import dashboard_screen
from src.app import app

app.layout = dashboard_screen

server = app.server

if __name__ == '__main__':
    app.run_server(debug=True)