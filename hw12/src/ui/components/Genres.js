import React from "react";

export default class Genres extends React.Component {
    constructor() {
        super();
        this.state = {genres: []};
    }

    componentDidMount() {
        fetch('/api/v1/genres')
            .then(response => response.json())
            .then(genres => this.setState({genres}));
    }

    render() {
        return (
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                </tr>
                </thead>
                <tbody>
                {
                    this.state.genres.map(genre => (
                        <tr key={genre.id}>
                            <td>{genre.id}</td>
                            <td>{genre.name}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }
}