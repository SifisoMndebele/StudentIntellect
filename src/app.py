import dash_bootstrap_components as dbc
from dash import Dash

app = Dash(__name__, external_stylesheets=[dbc.themes.MINTY, dbc.icons.FONT_AWESOME])
app.title = 'Student Intellect'
app._favicon = 'favicon.png'
# app.config.suppress_callback_exceptions = True