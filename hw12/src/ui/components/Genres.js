import React from "react";

export default class Genres extends React.Component {
    constructor() {
        super();
        this.state = {genres: []};
    }

    componentDidMount() {
        fetch('/api/v1/genres', {
            headers: {
                'Content-Type': 'application/json', // Optional, include if needed
                'Authorization': `Bearer ${localStorage.getItem('token')}` // Add Bearer token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch genres');
                }
                return response.json();
            })
            .then(genres => this.setState({ genres }))
            .catch(error => {
                console.error('Error fetching genres:', error);
                // Optionally handle the error (e.g., set error state or redirect)
            });
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