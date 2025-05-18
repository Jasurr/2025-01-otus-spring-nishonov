import React from "react";

export default class Genres extends React.Component {
    constructor() {
        super();
        this.state = { genres: [] };
    }

    componentDidMount() {
        // fetch('/api/v1/genres')
        //     .then(response => response.json())
        //     .then(genres => this.setState({ genres }));
    }

    render() {
        return (
            <div className="genres">
                <h2>Genres</h2>
                <ul>
                    {this.state.genres.map(genre => (
                        <li key={genre.id}>{genre.name}</li>
                    ))}
                </ul>
            </div>
        );
    }
}