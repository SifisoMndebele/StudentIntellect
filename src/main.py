from src.app import app
from dashboard.ui import dashboard_screen
import dash_bootstrap_components as dbc
from dash import Dash, html, dcc, Output, Input, callback
import plotly.express as px

app.layout = dashboard_screen

server = app.server

if __name__ == '__main__':
    app.run_server(debug=True)



# @callback(
#     Output("histogram-chart", "figure"),
#     Input("names", "value"), )
# def generate_histogram_chart(names):
#     df = px.data.tips()
#     fig = px.histogram(df, x="total_bill", nbins=20)
#     fig.update_layout(template='plotly_dark',
#                       plot_bgcolor='rgba(0, 0, 0, 0)',
#                       paper_bgcolor='rgba(0, 0, 0, 0)', )
#     return fig


# @callback(
#     Output("line-graph-chart", "figure"),
#     Input("names", "value"), )
# def generate_line_graph_chart(names):
#     df = px.data.stocks()
#     fig = px.line(df, x='date', y="GOOG")
#     fig.update_layout(template='plotly_dark',
#                       plot_bgcolor='rgba(0, 0, 0, 0)',
#                       paper_bgcolor='rgba(0, 0, 0, 0)', )


# @app.callback(
#     Output('popular-products-graph', 'figure'),
#     [Input('search-input', 'value')]
# )
# def update_graph(search_query):
#     df_filtered = fetch_popular_products(search_query)
#     fig = px.bar(df_filtered, x='total_numberOf_orders', y='Name', orientation='h', title='Most Popular Products')
#     fig.update_layout(width=1500, height=900, margin=dict(l=100, r=100, t=50, b=50), bargap=0.2)
#     fig.update_traces(hovertemplate='Product: %{y}<br>Total Orders: %{x}<br>Profit Margin: %{text}',
#                       text=df_filtered['profit_margin'])
#     return fig


# @callback(
#     Output("shop-name-display", "children"),
#     Input("store-dropdown-menu", "value"),
# )
# def on_form_change(store_value):
#     return f"{store_value}."