import sys
import subprocess
from faster_whisper import WhisperModel

# Load the Whisper model once
model = WhisperModel("base", device="cpu", compute_type="int8")

# 🔁 Convert .webm to .wav before transcription
def convert_to_wav(input_webm, output_wav):
    subprocess.run([
        "ffmpeg", "-y", "-i", input_webm,
        "-ar", "16000", "-ac", "1", "-c:a", "pcm_s16le", output_wav
    ], check=True)

# 🎤 Transcribe audio using Whisper
def transcribe(audio_path):
    segments, _ = model.transcribe(audio_path, beam_size=5)
    result = " ".join([segment.text for segment in segments])
    return result.strip()

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("No audio file path provided", file=sys.stderr)
        sys.exit(1)

    audio_file = sys.argv[1]
    wav_file = "converted.wav"

    try:
        # Convert to WAV first
        convert_to_wav(audio_file, wav_file)

        # Then transcribe
        text = transcribe(wav_file)
        print(text)
    except Exception as e:
        print(f"Error during transcription: {str(e)}", file=sys.stderr)
        sys.exit(1)
