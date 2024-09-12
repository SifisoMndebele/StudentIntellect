from dash import Output, Input, html, callback

from dashboard.ui import dashboard_screen
from src.app import app
from src.auth.ui import login_screen

app.layout = login_screen

server = app.server

if __name__ == '__main__':
    app.run_server(debug=True)