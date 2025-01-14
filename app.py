from flask import Flask, request, jsonify, send_file
from PIL import Image
from deoldify.visualize import get_image_colorizer
from pathlib import Path
import uuid

app = Flask(__name__)

# Load the DeOldify colorizer model
colorizer = get_image_colorizer(artistic=True)

@app.route('/colorize', methods=['POST'])
def colorize_image():
    if 'image' not in request.files:
        return jsonify({'error': 'No file part'}), 400

    file = request.files['image']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    try:
        # Generate a unique filename for the input image
        unique_filename = f"input_{uuid.uuid4().hex}.jpg"
        temp_input_path = Path(f'./temp/{unique_filename}')
        temp_input_path.parent.mkdir(parents=True, exist_ok=True)  # Ensure the directory exists
        
        # Save the uploaded image
        file.save(temp_input_path)

        # Perform colorization
        colorized_path = colorizer.plot_transformed_image(
            path=temp_input_path,
            render_factor=15,
            figsize=(10, 10),
            results_dir=Path('./results')
        )

        # Load and save the colorized image with a unique filename
        output_path = Path(f'./results/colorized_{uuid.uuid4().hex}.jpg')
        output_path.parent.mkdir(parents=True, exist_ok=True)  # Create the directory if it doesn't exist
        colorized_image = Image.open(colorized_path)
        colorized_image.save(output_path)

        # Return the colorized image file
        return send_file(output_path, mimetype='image/jpeg')
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
