import dash_bootstrap_components as dbc
from dash import Dash, html, dcc, Output, Input, callback
from ui import home_content, store_content, products_content
# from backend import store_table_df, selling_product_df, popular_products_df, sales_amount_df, top_ten_products_df
import plotly.express as px
import plotly.graph_objects as go
from plotly import data

app = Dash(__name__, external_stylesheets=[dbc.themes.VAPOR])
app.config.suppress_callback_exceptions = True

sidebar = html.Div(
    [
        html.A(
            dbc.Row(
                [
                    dbc.Col(html.Img(src=app.get_asset_url('bike_logo.png'), height="92px", width='auto')),
                    dbc.Col(html.H1("Marigold", className="ms-1")),
                ],
                align="start",
                className="g-0",
            ),
            href="/",
            style={"textDecoration": "none"},
        ),
        html.Hr(),
        dbc.Nav(
            [
                dbc.NavLink("Dashboard", href="/", active="exact"),
                dbc.NavLink("Store Information", href="/store_info", active="exact"),
                dbc.NavLink("Products", href="/products", active="exact"),
            ],
            vertical=True,
            pills=True,
        ),
    ],
    style={
        "position": "fixed",
        "top": 0,
        "left": 0,
        "bottom": 0,
        "width": "16rem",
        "padding": "2rem 1rem",
        "background-color": "#f8f9fa09",
    },
)

app.layout = html.Div([
    dcc.Location(id="url"),
    sidebar,
    html.Div(id="page-content", style={"margin-left": "16rem", })
])


@callback(Output("page-content", "children"),
          Input("url", "pathname"))
def render_page_content(pathname):
    if pathname == "/":
        return home_content
    elif pathname == "/store_info":
        return store_content
    elif pathname == "/products":
        return products_content
    # If the user tries to reach a different page, return a 404 message
    return html.Div(
        [
            html.H1("404: Not found", className="text-danger"),
            html.Hr(),
            html.P(f"The pathname {pathname} was not recognised..."),
        ],
        className="p-3 bg-light rounded-3",
    )



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


@callback(
    Output("line-graph-chart", "figure"),
    Input("names", "value"), )
def generate_line_graph_chart(names):
    df = px.data.stocks()
    fig = px.line(df, x='date', y="GOOG")
    fig.update_layout(template='plotly_dark',
                      plot_bgcolor='rgba(0, 0, 0, 0)',
                      paper_bgcolor='rgba(0, 0, 0, 0)', )


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




@callback(
    Output("shop-name-display", "children"),
    Input("store-dropdown-menu", "value"),
)
def on_form_change(store_value):
    return f"{store_value}."


if __name__ == '__main__':
    app.run_server(debug=True)
